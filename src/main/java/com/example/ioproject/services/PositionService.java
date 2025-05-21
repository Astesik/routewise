package com.example.ioproject.services;

import com.example.ioproject.dto.ItalyStaySummaryDto;
import com.example.ioproject.models.Device;
import com.example.ioproject.models.Position;
import com.example.ioproject.repository.PositionRepository;
import com.example.ioproject.repository.DeviceRepository;
import com.example.ioproject.dto.PositionDetailsProjection;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.*;

@Service
public class PositionService {
    private final GpsService gpsService;
    private final PositionRepository positionRepository;
    private final DeviceRepository deviceRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Dobierz do liczby trucków i mocy serwera


    public PositionService(GpsService gpsService, PositionRepository positionRepository, DeviceRepository deviceRepository) {
        this.gpsService = gpsService;
        this.positionRepository = positionRepository;
        this.deviceRepository = deviceRepository;
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
                positionRepository.save(newPosition);
            }
        }
    }

    public List<PositionDetailsProjection> getAllPositions() {
        return positionRepository.getPositionDetails();
    }

    public Map<String, Long> getVehicleCountByCountry() {
        List<PositionDetailsProjection> positions = positionRepository.getPositionDetails();
        return positions.stream()
                .filter(p -> p.getCountryCode() != null)
                .collect(Collectors.groupingBy(PositionDetailsProjection::getCountryCode, Collectors.counting()));
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

    // Endpoint 1: historia dzisiaj dla pojedynczego pojazdu
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

        List<ItalyStaySummaryDto> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return results;
    }

    private ItalyStaySummaryDto calculateItalyCurrentStay(List<Map<String, Object>> positionList, String deviceName, ZonedDateTime nowZdt, ZonedDateTime fromZdt) {
        // Sortujemy po timestampie malejąco (od najnowszego do najstarszego)
        positionList.sort((a, b) -> Long.compare(extractTimestampSafe(b), extractTimestampSafe(a)));

        Long arrivalTime = null;
        boolean foundNonItaly = false;

        // Przeglądamy pozycje od najnowszych do najstarszych
        for (Map<String, Object> pos : positionList) {
            Map<String, Object> country = (Map<String, Object>) pos.get("country");
            String countryCode = country != null ? (String) country.get("code") : null;
            if (countryCode == null) continue; // ignoruj nulle

            long timestamp = extractTimestamp(pos);

            if (!"IT".equals(countryCode)) {
                // Pierwszy wpis w innym kraju -> kończ, arrival = następny wpis
                foundNonItaly = true;
                break;
            }
            arrivalTime = timestamp; // Zapamiętuj czas, bo idziemy wstecz – najstarszy z IT to arrival
        }

        // Jeśli auto jest we Włoszech od najstarszego punktu zakresu (nie znaleziono wcześniejszego kraju), arrival = fromZdt
        if (arrivalTime == null) {
            arrivalTime = fromZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
        }

        long exitTime = nowZdt.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();

        if (arrivalTime != null && exitTime >= arrivalTime) {
            return new ItalyStaySummaryDto(
                    deviceName,
                    epochToZonedString(arrivalTime),
                    epochToZonedString(exitTime),
                    formatDuration(exitTime - arrivalTime)
            );
        }
        return null;
    }

    // Wyciąga timestamp epoch z pozycji GPS API (dostosuj do swojego formatu)
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

    // Safe extractor for sorting
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
                // obsłuż wyjątek, możesz logować np.
                System.err.println("Błąd pobierania historii device " + deviceId + " od " + currentFrom + " do " + currentTo + ": " + e.getMessage());
            }
            currentFrom = currentTo; // następny chunk
        }
        return allPositions;
    }
}
