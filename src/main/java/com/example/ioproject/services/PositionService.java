package com.example.ioproject.services;

import com.example.ioproject.models.Position;
import com.example.ioproject.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PositionService {
    private final GpsService gpsService;
    private final PositionRepository positionRepository;

    public PositionService(GpsService gpsService, PositionRepository positionRepository) {
        this.gpsService = gpsService;
        this.positionRepository = positionRepository;
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

            // Nowe pola:
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

                // Nowe pola:
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

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Map<String, Long> getVehicleCountByCountry() {
        List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .filter(p -> p.getCountryCode() != null)
                .collect(Collectors.groupingBy(Position::getCountryCode, Collectors.counting()));
    }

    public List<Position> getLowFuelVehicles() {
        List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .filter(p -> p.getFuelLevelPerc() != null && p.getFuelLevelPerc() < 50)
                .collect(Collectors.toList());
    }

    public Map<String, List<Position>> getPositionsGroupedByCountry() {
        List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .filter(p -> p.getCountryCode() != null)
                .collect(Collectors.groupingBy(Position::getCountryCode));
    }
}
