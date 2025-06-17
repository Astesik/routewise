package com.example.ioproject.vehicle.controller;

import com.example.ioproject.vehicle.dto.VehicleDetailsDto;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Endpoint: Pobierz listę pojazdów (dla wszystkich autoryzowanych użytkowników)
    @GetMapping("/get")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // Endpoint: Pobierz pojazd po ID (dla wszystkich autoryzowanych użytkowników)
    @GetMapping("/get/{id}")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Optional<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @GetMapping("/by-device/{deviceId}")
    public Optional<Vehicle> getVehicleByDeviceId(@PathVariable String deviceId) {
        return vehicleService.getVehicleByDeviceId(deviceId);
    }

    @GetMapping("/details/by-device/{deviceId}")
    public ResponseEntity<VehicleDetailsDto> getVehicleDetailsByDeviceId(@PathVariable String deviceId) {
        Optional<VehicleDetailsDto> details = vehicleService.getVehicleDetailsByDeviceId(deviceId);
        return details.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get/type/{type}")
    public List<Vehicle> getVehiclesByType(@PathVariable String type) {
        return vehicleService.getVehiclesByType(type);
    }

    // Endpoint: Dodaj nowy pojazd (dla admina)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Vehicle addVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.saveVehicle(vehicle);
    }

    // Endpoint: Zaktualizuj pojazd (dla admina)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Vehicle updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id); // Ustawiamy ID, aby dokonać aktualizacji istniejącego pojazdu
        return vehicleService.saveVehicle(vehicle);
    }

    // Endpoint: Usuń pojazd (dla admina)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}