package com.example.ioproject.scheduler;

import com.example.ioproject.device.service.DeviceService;
import com.example.ioproject.driver.service.DriverService;
import com.example.ioproject.position.service.PositionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceSyncScheduler {

    private final DeviceService deviceService;
    private final DriverService driverService;
    private final PositionService positionService;

    public DeviceSyncScheduler(DeviceService deviceService, DriverService driverService, PositionService positionService) {
        this.deviceService = deviceService;
        this.driverService = driverService;
        this.positionService = positionService;
    }

    @Scheduled(fixedRate = 30000)
    public void syncDevices() {
        deviceService.syncDevices();
    }

    @Scheduled(fixedRate = 30000)
    public void syncDrivers() {
        driverService.syncDrivers();
    }

    @Scheduled(fixedRate = 30000)
    public void syncPositions() {
        positionService.syncPositions();
    }

    @Scheduled(fixedRate = 300000) // 5 minut
    public void syncAbergPositions() {
        positionService.syncAbergPositions();
    }
}
