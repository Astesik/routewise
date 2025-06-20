package com.example.ioproject.device.controller;

import com.example.ioproject.device.dto.DeviceDto;
import com.example.ioproject.device.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/get")
    public List<DeviceDto> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/get/{id}")
    public DeviceDto getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DeviceDto addDevice(@Valid @RequestBody DeviceDto dto) {
        return deviceService.addDevice(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DeviceDto updateDevice(@PathVariable Long id, @RequestBody DeviceDto dto) {
        return deviceService.updateDevice(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public void syncDevices() {
        deviceService.syncDevices();
    }
}
