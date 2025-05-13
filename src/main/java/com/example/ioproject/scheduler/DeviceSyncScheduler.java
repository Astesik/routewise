package com.example.ioproject.scheduler;

import com.example.ioproject.services.DeviceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceSyncScheduler {
    private final DeviceService deviceService;

    public DeviceSyncScheduler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Scheduled(fixedRate = 30000)
    public void syncDevices() {
        deviceService.syncDevices();
    }
}
