package com.example.ioproject.scheduler;

import com.example.ioproject.device.service.DeviceService;
import com.example.ioproject.driver.service.DriverService;
import com.example.ioproject.position.service.PositionService;
import com.example.ioproject.position.service.VignetteCheckerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceSyncScheduler {

    private final DeviceService deviceService;
    private final DriverService driverService;
    private final PositionService positionService;
    private final VignetteCheckerService vignetteCheckerService;

    public DeviceSyncScheduler(DeviceService deviceService, DriverService driverService, PositionService positionService, VignetteCheckerService vignetteCheckerService) {
        this.deviceService = deviceService;
        this.driverService = driverService;
        this.positionService = positionService;
        this.vignetteCheckerService = vignetteCheckerService;
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

    @Scheduled(fixedRate = 30000) // co 30 sekund
    public void checkUkVignetteValidity() {
        vignetteCheckerService.checkUkVignettes();
    }

    @Scheduled(fixedRate = 300000) // co 5 minut
    public void printCountrySummaryNoCacheAsync() {
        positionService.printCountrySummaryNoCacheAsync();
    }
}
