package com.example.ioproject.models;

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

    // Pusty konstruktor (wymagany przez JPA)
    public Position() {
    }

    // Konstruktor z parametrami
    public Position(String deviceId, String countryCode, Double fuelLevelPerc, Double latitude, Double longitude, LocalDateTime updatedAt) {
        this.deviceId = deviceId;
        this.countryCode = countryCode;
        this.fuelLevelPerc = fuelLevelPerc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = updatedAt;
    }

    // gettery i settery

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
}
