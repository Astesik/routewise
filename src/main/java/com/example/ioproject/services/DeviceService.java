package com.example.ioproject.services;

import com.example.ioproject.models.Device;
import com.example.ioproject.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final GpsService gpsService;

    public DeviceService(DeviceRepository deviceRepository, GpsService gpsService) {
        this.deviceRepository = deviceRepository;
        this.gpsService = gpsService;
    }

    public void syncDevices() {
        Map<String, Object> response = gpsService.getPositions();
        List<Map<String, Object>> positions = (List<Map<String, Object>>) response.get("positionList");

        for (Map<String, Object> pos : positions) {
            String serialNumber = (String) pos.get("deviceId");

            Optional<Device> existingDevice = deviceRepository.findBySerialNumber(serialNumber);

            if (existingDevice.isEmpty()) {
                Device newDevice = new Device(
                        null,
                        null,
                        "new",
                        serialNumber,
                        LocalDateTime.now()
                );
                deviceRepository.save(newDevice);
            }
        }
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
}
