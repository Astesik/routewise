// controller/DriverController.java
package com.example.ioproject.driver.controller;

import com.example.ioproject.driver.dto.DriverDto;
import com.example.ioproject.driver.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/get")
    public List<DriverDto> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/get/{id}")
    public DriverDto getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PostMapping
    public DriverDto addDriver(@Valid @RequestBody DriverDto dto) {
        return driverService.addDriver(dto);
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
