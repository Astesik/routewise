package com.example.ioproject.dto;

/**
 * DTO do raportowania pobytu pojazdu we Włoszech (Italy).
 * Używane np. do endpointu raportującego pobyt trucka w IT.
 */
public class ItalyStaySummaryDto {
    private String deviceName;
    private String driverFullName;
    private String arrivalTime; // ISO-8601 z timezone, np. "2025-05-21T01:25:00+02:00"
    private String exitTime;    // jw. - jeśli pojazd nadal w kraju, to czas aktualny
    private String summaryTime; // np. "2d 4h 30m"

    public ItalyStaySummaryDto() {}

    public ItalyStaySummaryDto(String deviceName,String driverFullName, String arrivalTime, String exitTime, String summaryTime) {
        this.deviceName = deviceName;
        this.driverFullName = driverFullName;
        this.arrivalTime = arrivalTime;
        this.exitTime = exitTime;
        this.summaryTime = summaryTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDriverFullName() {
        return driverFullName;
    }

    public void setDriverFullName(String driverFullName) {
        this.driverFullName = driverFullName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public String getSummaryTime() {
        return summaryTime;
    }

    public void setSummaryTime(String summaryTime) {
        this.summaryTime = summaryTime;
    }

    @Override
    public String toString() {
        return "ItalyStaySummaryDto{" +
                "deviceName='" + deviceName + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", exitTime='" + exitTime + '\'' +
                ", summaryTime='" + summaryTime + '\'' +
                '}';
    }
}
