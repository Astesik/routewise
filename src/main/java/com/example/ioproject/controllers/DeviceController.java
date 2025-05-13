package com.example.ioproject.controllers;

import com.example.ioproject.models.Device;
import com.example.ioproject.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.ioproject.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    // Pobierz wszystkie urządzenia
    @GetMapping("/get")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    // Pobierz urządzenie po ID
    @GetMapping("/get/{id}")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Optional<Device> getDeviceById(@PathVariable Long id) {
        return deviceRepository.findById(id);
    }

    // Dodaj nowe urządzenie
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Device addDevice(@RequestBody Device device) {
        return deviceRepository.save(device);
    }

    // Aktualizuj urządzenie
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Device updateDevice(@PathVariable Long id, @RequestBody Device device) {
        // Pobierz istniejące urządzenie z bazy danych
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found for this id :: " + id));

        // Aktualizuj tylko te pola, które są przekazane w żądaniu (nie null)
        if (device.getDeviceName() != null) {
            existingDevice.setDeviceName(device.getDeviceName());
        }

        if (device.getSerialNumber() != null) {
            existingDevice.setSerialNumber(device.getSerialNumber());
        }

        if (device.getType() != null) {
            existingDevice.setType(device.getType());
        }

        if (device.getStatus() != null) {
            existingDevice.setStatus(device.getStatus());
        }

        // Jeśli 'created_at' jest null, nie zmieniamy tego pola, ponieważ nie ma sensu go modyfikować w procesie aktualizacji
        if (device.getCreatedAt() != null) {
            existingDevice.setCreatedAt(device.getCreatedAt());
        }

        // Zapisz zmodyfikowany obiekt w bazie danych
        return deviceRepository.save(existingDevice);
    }

    // Usuń urządzenie
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDevice(@PathVariable Long id) {
        deviceRepository.deleteById(id);
    }
}
