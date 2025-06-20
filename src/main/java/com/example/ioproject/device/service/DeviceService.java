package com.example.ioproject.device.service;

import com.example.ioproject.device.dto.DeviceDto;
import com.example.ioproject.device.exception.DeviceNotFoundException;
import com.example.ioproject.device.model.Device;
import com.example.ioproject.device.repository.DeviceRepository;
import com.example.ioproject.services.GpsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final GpsService gpsService;

    public DeviceService(DeviceRepository deviceRepository, GpsService gpsService) {
        this.deviceRepository = deviceRepository;
        this.gpsService = gpsService;
    }

    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DeviceDto getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found for id: " + id));
        return toDto(device);
    }

    public DeviceDto addDevice(DeviceDto dto) {
        Device device = toEntity(dto);
        device.setCreatedAt(LocalDateTime.now());
        Device saved = deviceRepository.save(device);
        return toDto(saved);
    }

    public DeviceDto updateDevice(Long id, DeviceDto dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found for id: " + id));

        if (dto.getDeviceName() != null) device.setDeviceName(dto.getDeviceName());
        if (dto.getType() != null) device.setType(dto.getType());
        if (dto.getStatus() != null) device.setStatus(dto.getStatus());
        if (dto.getSerialNumber() != null) device.setSerialNumber(dto.getSerialNumber());
        if (dto.getCreatedAt() != null) device.setCreatedAt(dto.getCreatedAt());

        Device saved = deviceRepository.save(device);
        return toDto(saved);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new DeviceNotFoundException("Device not found for id: " + id);
        }
        deviceRepository.deleteById(id);
    }

    public void syncDevices() {
        Map<String, Object> response = gpsService.getPositions();
        List<Map<String, Object>> positions = (List<Map<String, Object>>) response.get("positionList");

        for (Map<String, Object> pos : positions) {
            String serialNumber = (String) pos.get("deviceId");
            if (deviceRepository.findBySerialNumber(serialNumber).isEmpty()) {
                Device newDevice = new Device(
                        null, null, "new", serialNumber, LocalDateTime.now()
                );
                deviceRepository.save(newDevice);
            }
        }
    }

    private DeviceDto toDto(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setDeviceName(device.getDeviceName());
        dto.setType(device.getType());
        dto.setStatus(device.getStatus());
        dto.setSerialNumber(device.getSerialNumber());
        dto.setCreatedAt(device.getCreatedAt());
        return dto;
    }

    private Device toEntity(DeviceDto dto) {
        Device device = new Device();
        device.setDeviceName(dto.getDeviceName());
        device.setType(dto.getType());
        device.setStatus(dto.getStatus());
        device.setSerialNumber(dto.getSerialNumber());
        device.setCreatedAt(dto.getCreatedAt());
        return device;
    }
}
