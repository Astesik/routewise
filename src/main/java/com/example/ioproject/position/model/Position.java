package com.example.ioproject.position.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "fuel_level_perc")
    private Double fuelLevelPerc;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Nowe pola:
    @Column(name = "ignition_state")
    private String ignitionState;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "heading")
    private Integer heading;

    @Column(name = "driver_slot_0")
    private String driverSlot0;

    @Column(name = "driver_slot_1")
    private String driverSlot1;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    // Pusty konstruktor (wymagany przez JPA)
    public Position() {
    }

    // Konstruktor z parametrami
    public Position(String deviceId, String countryCode, Double fuelLevelPerc, Double latitude, Double longitude,
                    LocalDateTime updatedAt, String ignitionState, Double speed, Integer heading,
                    String driverSlot0, String driverSlot1) {
        this.deviceId = deviceId;
        this.countryCode = countryCode;
        this.fuelLevelPerc = fuelLevelPerc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = updatedAt;
        this.ignitionState = ignitionState;
        this.speed = speed;
        this.heading = heading;
        this.driverSlot0 = driverSlot0;
        this.driverSlot1 = driverSlot1;
    }

    // GETTERY I SETTERY

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getFuelLevelPerc() {
        return fuelLevelPerc;
    }

    public void setFuelLevelPerc(Double fuelLevelPerc) {
        this.fuelLevelPerc = fuelLevelPerc;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIgnitionState() {
        return ignitionState;
    }

    public void setIgnitionState(String ignitionState) {
        this.ignitionState = ignitionState;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getHeading() {
        return heading;
    }

    public void setHeading(Integer heading) {
        this.heading = heading;
    }

    public String getDriverSlot0() {
        return driverSlot0;
    }

    public void setDriverSlot0(String driverSlot0) {
        this.driverSlot0 = driverSlot0;
    }

    public String getDriverSlot1() {
        return driverSlot1;
    }

    public void setDriverSlot1(String driverSlot1) {
        this.driverSlot1 = driverSlot1;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
