// TimelineBlockDto.java
package com.example.ioproject.position.dto;

public class TimelineBlockDto {
    private String type;
    private long duration;
    private long startTs;
    private long endTs;
    private String deviceId;
    private String vehicleName;
    private String country;
    private String driverName;

    // Konstruktor
    public TimelineBlockDto(String type, long duration, long startTs, long endTs, String deviceId, String vehicleName, String country, String driverName) {
        this.type = type;
        this.duration = duration;
        this.startTs = startTs;
        this.endTs = endTs;
        this.deviceId = deviceId;
        this.vehicleName = vehicleName;
        this.country = country;
        this.driverName = driverName;
    }

    // GETTERY!
    public String getType() { return type; }
    public long getDuration() { return duration; }
    public long getStartTs() { return startTs; }
    public long getEndTs() { return endTs; }
    public String getDeviceId() { return deviceId; }
    public String getVehicleName() { return vehicleName; }
    public String getCountry() { return country; }
    public String getDriverName() { return driverName; }
}
