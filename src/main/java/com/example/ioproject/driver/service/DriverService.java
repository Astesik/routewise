// service/DriverService.java
package com.example.ioproject.driver.service;

import com.example.ioproject.driver.dto.DriverDto;
import com.example.ioproject.driver.exception.DriverNotFoundException;
import com.example.ioproject.driver.model.Driver;
import com.example.ioproject.driver.repository.DriverRepository;
import com.example.ioproject.services.GpsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final GpsService gpsService;

    public DriverService(DriverRepository driverRepository, GpsService gpsService) {
        this.driverRepository = driverRepository;
        this.gpsService = gpsService;
    }

    public List<DriverDto> getAllDrivers() {
        return driverRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found: " + id));
        return toDto(driver);
    }

    public DriverDto addDriver(DriverDto dto) {
        Driver driver = toEntity(dto);
        Driver saved = driverRepository.save(driver);
        return toDto(saved);
    }

    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new DriverNotFoundException("Driver not found: " + id);
        }
        driverRepository.deleteById(id);
    }

    public void syncDrivers() {
        Map<String, Object> response = gpsService.getDrivers();
        List<Map<String, Object>> drivers = (List<Map<String, Object>>) response.get("drivers");

        for (Map<String, Object> driverData : drivers) {
            Long tachoid = Long.valueOf(driverData.get("tachoid").toString());

            if (driverRepository.findByTachoid(tachoid).isEmpty()) {
                Driver newDriver = new Driver(
                        tachoid,
                        (String) driverData.get("firstname"),
                        (String) driverData.get("lastname"),
                        driverData.get("deviceid") != null ? Long.valueOf(driverData.get("deviceid").toString()) : null
                );
                driverRepository.save(newDriver);
            }
        }
    }

    private DriverDto toDto(Driver driver) {
        DriverDto dto = new DriverDto();
        dto.setId(driver.getId());
        dto.setTachoid(driver.getTachoid());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setDeviceId(driver.getDeviceId());
        return dto;
    }

    private Driver toEntity(DriverDto dto) {
        Driver driver = new Driver();
        if (dto.getId() != null) driver.setId(dto.getId());
        driver.setTachoid(dto.getTachoid());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setDeviceId(dto.getDeviceId());
        return driver;
    }
}
