package com.example.ioproject.driver.service;

import com.example.ioproject.driver.model.Driver;
import com.example.ioproject.driver.repository.DriverRepository;
import com.example.ioproject.services.GpsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final GpsService gpsService;

    public DriverService(DriverRepository driverRepository, GpsService gpsService) {
        this.driverRepository = driverRepository;
        this.gpsService = gpsService;
    }

    public void syncDrivers() {
        Map<String, Object> response = gpsService.getDrivers();
        List<Map<String, Object>> drivers = (List<Map<String, Object>>) response.get("drivers");

        for (Map<String, Object> driverData : drivers) {
            Long tachoid = Long.valueOf(driverData.get("tachoid").toString());

            Optional<Driver> existingDriver = driverRepository.findByTachoid(tachoid);

            if (existingDriver.isEmpty()) {
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

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }
}
