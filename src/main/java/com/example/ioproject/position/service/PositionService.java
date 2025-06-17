package com.example.ioproject.position.service;

import com.example.ioproject.position.dto.ItalyStaySummaryDto;
import com.example.ioproject.device.model.Device;
import com.example.ioproject.position.model.Position;
import com.example.ioproject.vehicle.model.VehicleGroup;
import com.example.ioproject.position.repository.PositionRepository;
import com.example.ioproject.device.repository.DeviceRepository;
import com.example.ioproject.driver.repository.DriverRepository;
import com.example.ioproject.position.dto.PositionDetailsProjection;
import com.example.ioproject.services.GpsService;
import com.example.ioproject.vehicle.service.VehicleGroupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PositionService {
    private final GpsService gpsService;
    private final PositionRepository positionRepository;
    private final DeviceRepository deviceRepository;
    private final DriverRepository driverRepository;
    private final VehicleGroupService vehicleGroupService;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    // -- Nowe dla AbergTelematics --
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<String> abergAccessToken = new AtomicReference<>(null);
    private final AtomicReference<LocalDateTime> abergTokenExpiry = new AtomicReference<>(null);

    @Value("${aberg.client-id}")
    private String abergClientId;

    @Value("${aberg.client-secret}")
    private String abergClientSecret;

    @Value("${aberg.scope}")
    private String abergScope;

    public PositionService(
            GpsService gpsService,
            PositionRepository positionRepository,
            DriverRepository driverRepository,
            DeviceRepository deviceRepository,
            VehicleGroupService vehicleGroupService
    ) {
        this.gpsService = gpsService;
        this.positionRepository = positionRepository;
        this.deviceRepository = deviceRepository;
        this.vehicleGroupService = vehicleGroupService;
        this.driverRepository = driverRepository;
    }

    // --- DODANE: Synchronizacja pozycji AbergTelematics ---
    public void syncAbergPositions() {
        try {
            String token = abergAccessToken.get();
            LocalDateTime expiry = abergTokenExpiry.get();
            if (token == null || expiry == null || expiry.isBefore(LocalDateTime.now().plusMinutes(5))) {
                token = fetchAbergAccessToken();
            }
            if (token == null) {
                System.err.println("Nie udało się pobrać tokena AbergTelematics");
                return;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.abergtelematics.com/vehicles-module/vehicles/map-view?v=1",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) return;
            List<Map<String, Object>> collection = (List<Map<String, Object>>) responseBody.get("collection");
            if (collection == null) return;

            for (Map<String, Object> vehicle : collection) {
                String vin = (String) vehicle.get("vin");
                String deviceName = (String) vehicle.get("label"); // Twój kod taborowy, można dać na device_name
                // Możesz dodać detekcję typu np. "trailer", jeśli gdzieś jest
                String type = "trailer"; // domyślnie, zmień jeśli chcesz!

                Map<String, Object> gps = (Map<String, Object>) vehicle.get("gps");
                Integer heading = gps != null && gps.get("head") != null ? Integer.valueOf(gps.get("head").toString()) : null;
                Double latitude = gps != null && gps.get("latitude") != null ? Double.valueOf(gps.get("latitude").toString()) : null;
                Double longitude = gps != null && gps.get("longitude") != null ? Double.valueOf(gps.get("longitude").toString()) : null;

                // --- Pozycja ---
                if (vin == null || latitude == null || longitude == null) continue;
                latitude = roundTo6(latitude);
                longitude = roundTo6(longitude);

                Optional<Position> opt = positionRepository.findByDeviceId(vin);
                Position pos = opt.orElse(new Position());
                pos.setDeviceId(vin);
                pos.setLatitude(latitude);
                pos.setLongitude(longitude);
                pos.setHeading(heading);
                pos.setUpdatedAt(LocalDateTime.now().minusHours(2));
                pos.setReceivedAt(LocalDateTime.now().minusHours(2));

                positionRepository.save(pos);

                // --- Device ---
                Optional<Device> existingDevice = deviceRepository.findBySerialNumber(vin);
                if (existingDevice.isEmpty()) {
                    // NOWY DEVICE
                    Device newDevice = new Device(
                            null,
                            null,
                            "new",
                            vin,
                            LocalDateTime.now()
                    );
                    deviceRepository.save(newDevice);
                } else {
//                    System.out.println("Urządzenie już w systemie.");
                }
            }
            System.out.println("Pobrano i zapisano pozycje + devices z AbergTelematics: " + collection.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- DODANE: Pobieranie tokena ---
    private String fetchAbergAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body =
                    "grant_type=client_credentials" +
                            "&client_id=" + abergClientId +
                            "&client_secret=" + abergClientSecret +
                            "&scope=" + abergScope;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://login.abergtelematics.com/0a772018-3a46-441b-b686-a8dee2419571/B2C_1A_SIGNIN_APPLICATION/oauth2/v2.0/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");
            if (accessToken != null && expiresIn != null) {
                abergAccessToken.set(accessToken);
                abergTokenExpiry.set(LocalDateTime.now().plusSeconds(expiresIn - 60)); // margines minuty
                return accessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void syncPositions() {
        Map<String, Object> response = gpsService.getPositions();
        List<Map<String, Object>> positions = (List<Map<String, Object>>) response.get("positionList");

        for (Map<String, Object> pos : positions) {
            String deviceId = String.valueOf(pos.get("deviceId"));
            Map<String, Object> country = (Map<String, Object>) pos.get("country");
            String countryCode = country != null ? (String) country.get("code") : null;
            Double fuelLevel = pos.get("fuellevelperc") != null ? Double.valueOf(pos.get("fuellevelperc").toString()) : null;

            Map<String, Object> coordinate = (Map<String, Object>) pos.get("coordinate");
            Double latitude = coordinate != null ? (Double) coordinate.get("latitude") : null;
            Double longitude = coordinate != null ? (Double) coordinate.get("longitude") : null;

            String ignitionState = pos.get("ignitionState") != null ? pos.get("ignitionState").toString() : null;
            Double speed = pos.get("speed") != null ? Double.valueOf(pos.get("speed").toString()) : null;
            Integer heading = pos.get("heading") != null ? Integer.valueOf(pos.get("heading").toString()) : null;

            List<Map<String, Object>> drivers = (List<Map<String, Object>>) pos.get("drivers");
            String driverSlot0 = null;
            String driverSlot1 = null;
            if (drivers != null) {
                if (drivers.size() > 0 && drivers.get(0).get("id") != null) {
                    driverSlot0 = drivers.get(0).get("id").toString();
                }
                if (drivers.size() > 1 && drivers.get(1).get("id") != null) {
                    driverSlot1 = drivers.get(1).get("id").toString();
                }
            }

            // Parsowanie pola dateTime
            LocalDateTime receivedAt = null;
            Map<String, Object> dateTimeMap = (Map<String, Object>) pos.get("dateTime");
            if (dateTimeMap != null) {
                try {
                    int year = (int) dateTimeMap.get("year");
                    int month = (int) dateTimeMap.get("month");
                    int day = (int) dateTimeMap.get("day");
                    int hour = (int) dateTimeMap.get("hour");
                    int minute = (int) dateTimeMap.get("minute");
                    int second = (int) dateTimeMap.get("seconds");
                    receivedAt = LocalDateTime.of(year, month, day, hour, minute, second);
                } catch (Exception ex) {
                    receivedAt = null;
                }
            }

            Optional<Position> existingPositionOpt = positionRepository.findByDeviceId(deviceId);

            if (existingPositionOpt.isPresent()) {
                Position existingPosition = existingPositionOpt.get();
                existingPosition.setCountryCode(countryCode);
                existingPosition.setFuelLevelPerc(fuelLevel);
                existingPosition.setLatitude(latitude);
                existingPosition.setLongitude(longitude);
                existingPosition.setUpdatedAt(LocalDateTime.now());
                existingPosition.setIgnitionState(ignitionState);
                existingPosition.setSpeed(speed);
                existingPosition.setHeading(heading);
                existingPosition.setDriverSlot0(driverSlot0);
                existingPosition.setDriverSlot1(driverSlot1);

                existingPosition.setReceivedAt(receivedAt);

                positionRepository.save(existingPosition);
            } else {
                Position newPosition = new Position(
                        deviceId,
                        countryCode,
                        fuelLevel,
                        latitude,
                        longitude,
                        LocalDateTime.now(),
                        ignitionState,
                        speed,
                        heading,
                        driverSlot0,
                        driverSlot1
                );
                newPosition.setReceivedAt(receivedAt);
                positionRepository.save(newPosition);
            }
        }
    }

    public List<PositionDetailsProjection> getAllPositions() {
        return positionRepository.getPositionDetails();
    }

    public List<PositionDetailsProjection> getTrucks() {
        return getAllPositions().stream()
                .filter(pos -> "truck".equalsIgnoreCase(pos.getType()))
                .collect(Collectors.toList());
    }

    public List<PositionDetailsProjection> getTrailers() {
        return getAllPositions().stream()
                .filter(pos -> "trailer".equalsIgnoreCase(pos.getType()))
                .collect(Collectors.toList());
    }

    public List<PositionDetailsProjection> getGroupVehicles(Long groupId) {
        Optional<VehicleGroup> groupOpt = vehicleGroupService.getGroupById(groupId);
        if (groupOpt.isEmpty()) return Collections.emptyList();
        VehicleGroup group = groupOpt.get();

        Set<String> deviceIdsInGroup = group.getVehicles().stream()
                .filter(v -> v.getDevice() != null)
                .map(v -> v.getDevice().getSerialNumber())
                .collect(Collectors.toSet());

        return getAllPositions().stream()
                .filter(pos -> deviceIdsInGroup.contains(pos.getDeviceId()))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getVehicleCountByCountry() {
        List<PositionDetailsProjection> positions = positionRepository.getPositionDetails();
        return positions.stream()
                .filter(p -> p.getCountryCode() != null)
                .collect(Collectors.groupingBy(PositionDetailsProjection::getCountryCode, Collectors.counting()));
    }

    public Map<String, Map<String, Long>> getVehicleCountByCountryAndType() {
        List<PositionDetailsProjection> positions = positionRepository.getPositionDetails();

        Map<String, Map<String, Long>> result = new HashMap<>();

        positions.stream()
                .filter(p -> p.getCountryCode() != null && p.getType() != null)
                .forEach(p -> {
                    String country = p.getCountryCode();
                    String type = p.getType().toLowerCase();

                    result.putIfAbsent(country, new HashMap<>());
                    Map<String, Long> countryMap = result.get(country);

                    countryMap.put(type, countryMap.getOrDefault(type, 0L) + 1);
                });

        for (Map.Entry<String, Map<String, Long>> entry : result.entrySet()) {
            long sum = entry.getValue().getOrDefault("truck", 0L) + entry.getValue().getOrDefault("trailer", 0L);
            entry.getValue().put("all", sum);
        }

        return result;
    }

    public List<PositionDetailsProjection> getLowFuelVehicles() {
        List<PositionDetailsProjection> positions = positionRepository.getPositionDetails();
        return positions.stream()
                .filter(p -> p.getFuelLevelPerc() != null && p.getFuelLevelPerc() < 50)
                .collect(Collectors.toList());
    }

    public Map<String, List<PositionDetailsProjection>> getPositionsGroupedByCountry() {
        List<PositionDetailsProjection> positions = positionRepository.getPositionDetails();
        return positions.stream()
                .filter(p -> p.getCountryCode() != null)
                .collect(Collectors.groupingBy(PositionDetailsProjection::getCountryCode));
    }

    public Map<String, Object> getTodayHistoryForDevice(String deviceId) {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zone);
        LocalDateTime todayStartLocal = today.atStartOfDay();
        LocalDateTime todayEndLocal = today.atTime(LocalTime.MAX);

        ZonedDateTime startZdt = todayStartLocal.atZone(zone).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endZdt = todayEndLocal.atZone(zone).withZoneSameInstant(ZoneOffset.UTC);

        long fromTimestamp = startZdt.toEpochSecond();
        long toTimestamp = endZdt.toEpochSecond();

        return gpsService.getHistory(deviceId, fromTimestamp, toTimestamp);
    }

    public List<ItalyStaySummaryDto> getItalyTrucksStaySummary(int days) {
        ZonedDateTime nowZdt = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        ZonedDateTime fromZdt = nowZdt.minusSeconds(days * 86400L);

        long fromTimestamp = fromZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
        long toTimestamp = nowZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();

        List<Device> italyTrucks = deviceRepository.findTrucksInCountry("IT");
        List<CompletableFuture<ItalyStaySummaryDto>> futures = new ArrayList<>();

        for (Device device : italyTrucks) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<Map<String, Object>> positionList = fetchFullHistory(device.getSerialNumber(), fromTimestamp, toTimestamp);
                if (positionList == null || positionList.isEmpty()) return null;
                return calculateItalyCurrentStay(positionList, device.getDeviceName(), nowZdt, fromZdt);
            }, executor));
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ItalyStaySummaryDto calculateItalyCurrentStay(List<Map<String, Object>> positionList, String deviceName, ZonedDateTime nowZdt, ZonedDateTime fromZdt) {
        // Sortujemy po timestampie malejąco
        positionList.sort((a, b) -> Long.compare(extractTimestampSafe(b), extractTimestampSafe(a)));

        Long arrivalTime = null;

        for (Map<String, Object> pos : positionList) {
            Map<String, Object> country = (Map<String, Object>) pos.get("country");
            String countryCode = country != null ? (String) country.get("code") : null;
            if (countryCode == null) continue;

            long timestamp = extractTimestamp(pos);

            if (!"IT".equals(countryCode)) {
                break;
            }
            arrivalTime = timestamp;
        }

        if (arrivalTime == null) {
            arrivalTime = fromZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
        }

        long exitTime = nowZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();

        // Pobierz dane kierowcy
        String driverFullName = null;
        Map<String, Object> firstPos = positionList.isEmpty() ? null : positionList.get(0);
        if (firstPos != null && firstPos.get("drivers") != null) {
            List<Map<String, Object>> drivers = (List<Map<String, Object>>) firstPos.get("drivers");
            if (drivers != null && !drivers.isEmpty()) {
                Map<String, Object> activeDriver = drivers.stream()
                        .filter(d -> Boolean.TRUE.equals(d.get("valid")) && Boolean.TRUE.equals(d.get("cardinslot")))
                        .findFirst()
                        .orElse(drivers.get(0)); // fallback

                String driverIdStr = String.valueOf(activeDriver.get("id"));
                Long driverId = null;
                try {
                    driverId = driverIdStr != null ? Long.valueOf(driverIdStr) : null;
                } catch (NumberFormatException ex) {
                    driverId = null;
                }

                if (driverId != null) {
                    driverFullName = driverRepository.findByTachoid(driverId)
                            .map(drv -> drv.getFirstName() + " " + drv.getLastName())
                            .orElse("Brak w bazie (" + driverId + ")");
                } else {
                    driverFullName = "Brak danych";
                }
            }
        }

        if (arrivalTime != null && exitTime >= arrivalTime) {
            return new ItalyStaySummaryDto(
                    deviceName,
                    driverFullName,
                    epochToZonedString(arrivalTime),
                    epochToZonedString(exitTime),
                    formatDuration(exitTime - arrivalTime)
            );
        }
        return null;
    }

    private Long extractTimestamp(Map<String, Object> pos) {
        Map<String, Object> dateTime = (Map<String, Object>) pos.get("dateTime");
        if (dateTime != null) {
            int year = (int) dateTime.get("year");
            int month = (int) dateTime.get("month");
            int day = (int) dateTime.get("day");
            int hour = (int) dateTime.get("hour");
            int minute = (int) dateTime.get("minute");
            int seconds = (int) dateTime.get("seconds");
            LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, seconds);
            return ldt.toEpochSecond(ZoneOffset.UTC);
        }
        return null;
    }

    private long extractTimestampSafe(Map<String, Object> pos) {
        Long ts = extractTimestamp(pos);
        return ts == null ? 0L : ts;
    }

    private String epochToZonedString(Long epoch) {
        if (epoch == null) return "";
        ZonedDateTime zdt = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("Europe/Warsaw"));
        return zdt.toString();
    }

    private String formatDuration(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        return String.format("%dd %dh %dm", days, hours, mins);
    }

    private static Double roundTo6(Double value) {
        return value == null ? null : Math.round(value * 1_000_000d) / 1_000_000d;
    }

    private List<Map<String, Object>> fetchFullHistory(String deviceId, long fromTimestamp, long toTimestamp) {
        final long CHUNK = 86400L; // 24h w sekundach
        List<Map<String, Object>> allPositions = new ArrayList<>();
        long currentFrom = fromTimestamp;
        while (currentFrom < toTimestamp) {
            long currentTo = Math.min(currentFrom + CHUNK, toTimestamp);
            try {
                Map<String, Object> historyChunk = gpsService.getHistory(deviceId, currentFrom, currentTo);
                List<Map<String, Object>> positionList = (List<Map<String, Object>>) historyChunk.get("positionList");
                if (positionList != null && !positionList.isEmpty()) {
                    allPositions.addAll(positionList);
                }
            } catch (Exception e) {
                System.err.println("Błąd pobierania historii device " + deviceId + " od " + currentFrom + " do " + currentTo + ": " + e.getMessage());
            }
            currentFrom = currentTo;
        }
        return allPositions;
    }
}
