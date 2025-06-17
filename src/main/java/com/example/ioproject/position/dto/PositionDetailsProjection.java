package com.example.ioproject.position.dto;

import java.time.LocalDateTime;

public interface PositionDetailsProjection {
    Long getId();
    String getDeviceId();
    String getDeviceName();
    String getCountryCode();
    Double getFuelLevelPerc();
    Double getLatitude();
    Double getLongitude();
    String getIgnitionState();
    Double getSpeed();
    Integer getHeading();
    String getType();
    String getDriverSlot0();
    String getDriver0FirstName();
    String getDriver0LastName();
    String getDriverSlot1();
    String getDriver1FirstName();
    String getDriver1LastName();
    LocalDateTime getReceivedAt();
}
