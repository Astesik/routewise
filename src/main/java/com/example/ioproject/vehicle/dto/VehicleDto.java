package com.example.ioproject.vehicle.dto;

public class VehicleDto {
    private Long id;
    private String licensePlate;
    private String vin;
    private String type;
    private String make;

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
}
