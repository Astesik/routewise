package com.example.ioproject.dto;

import com.example.ioproject.models.Device;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class VehicleDetailsDto {
    private Long id;
    private String licensePlate;
    private String vin;
    private LocalDate firstRegistration;
    private int productionYear;
    private String make;
    private String euroClass;
    private String type;
    private String ownership;
    private String ownershipUntil;
    private LocalDate technicalInspection;
    private Integer daysToReview;  // <- nowe pole
    private LocalDate tachographInspection;
    private Integer daysToTachograph;
    private Device device;

    public VehicleDetailsDto() {}

    public VehicleDetailsDto(com.example.ioproject.models.Vehicle v) {
        this.id = v.getId();
        this.licensePlate = v.getLicensePlate();
        this.vin = v.getVin();
        this.firstRegistration = v.getFirstRegistration();
        this.productionYear = v.getProductionYear();
        this.make = v.getMake();
        this.euroClass = v.getEuroClass();
        this.type = v.getType();
        this.ownership = v.getOwnership();
        this.ownershipUntil = v.getOwnershipUntil();
        this.technicalInspection = v.getTechnicalInspection();
        this.tachographInspection = v.getTachographInspection();
        this.device = v.getDevice();

        // Wyliczanie dni do przeglądu
        if (this.technicalInspection != null) {
            this.daysToReview = (int) ChronoUnit.DAYS.between(LocalDate.now(), this.technicalInspection);
        } else {
            this.daysToReview = null;
        }

        // Wyliczanie dni do tachografu
        if (this.tachographInspection != null) {
            this.daysToTachograph = (int) ChronoUnit.DAYS.between(LocalDate.now(), this.tachographInspection);
        } else {
            this.daysToTachograph = null;
        }
    }

    // Gettery i settery...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public LocalDate getFirstRegistration() {
        return firstRegistration;
    }

    public void setFirstRegistration(LocalDate firstRegistration) {
        this.firstRegistration = firstRegistration;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getEuroClass() {
        return euroClass;
    }

    public void setEuroClass(String euroClass) {
        this.euroClass = euroClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getOwnershipUntil() {
        return ownershipUntil;
    }

    public void setOwnershipUntil(String ownershipUntil) {
        this.ownershipUntil = ownershipUntil;
    }

    public LocalDate getTechnicalInspection() {
        return technicalInspection;
    }

    public void setTechnicalInspection(LocalDate technicalInspection) {
        this.technicalInspection = technicalInspection;
    }

    public LocalDate getTachographInspection() {
        return tachographInspection;
    }

    public void setTachographInspection(LocalDate tachographInspection) {
        this.tachographInspection = tachographInspection;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Integer getDaysToReview() {
        return daysToReview;
    }

    public void setDaysToReview(Integer daysToReview) {
        this.daysToReview = daysToReview;
    }

    public Integer getDaysToTachograph() { return daysToTachograph; }
    public void setDaysToTachograph(Integer daysToTachograph) { this.daysToTachograph = daysToTachograph; }


    // Reszta pól: standardowe get/set
}
