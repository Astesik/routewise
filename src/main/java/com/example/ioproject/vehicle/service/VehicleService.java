package com.example.ioproject.vehicle.service;

import com.example.ioproject.vehicle.dto.VehicleDetailsDto;
import com.example.ioproject.device.model.Device;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.device.repository.DeviceRepository;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getVehicleByDeviceId(String deviceId) {
        return vehicleRepository.findByDevice_SerialNumber(deviceId); // załóżmy, że device ma pole deviceId
    }

    public Optional<VehicleDetailsDto> getVehicleDetailsByDeviceId(String deviceId) {
        return vehicleRepository.findByDevice_SerialNumber(deviceId)
                .map(VehicleDetailsDto::new);
    }

    public List<Vehicle> getVehiclesByType(String type) {
        return vehicleRepository.findByType(type.toUpperCase());
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        if (vehicle.getDevice() != null && vehicle.getDevice().getId() != null) {
            Device device = deviceRepository.findById(vehicle.getDevice().getId())
                    .orElseThrow(() -> new RuntimeException("Device not found"));
            vehicle.setDevice(device);
        } else {
            vehicle.setDevice(null);
        }
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}