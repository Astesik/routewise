package com.example.ioproject.driver.controller;

import com.example.ioproject.driver.model.Driver;
import com.example.ioproject.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/get")
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/get/{id}")
    public Optional<Driver> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PostMapping
    public Driver addDriver(@RequestBody Driver driver) {
        return driverService.addDriver(driver);
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PostMapping("/sync")
    public void syncDrivers() {
        driverService.syncDrivers();
    }
}
