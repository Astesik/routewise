// dto/RepairDto.java
package com.example.ioproject.repair.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class RepairDto {
    public enum RepairStatus {
        PLANNED, IN_PROGRESS, DONE, CANCELLED
    }

    private Long id;

    @NotNull(message = "Pole vehicleId jest wymagane")
    private Long vehicleId;

    private String licensePlates;

    @NotBlank(message = "Opis nie może być pusty")
    private String description;

    @NotNull(message = "Data jest wymagana")
    private LocalDate plannedDate;

    @NotNull(message = "Godzina jest wymagana")
    private LocalTime plannedTime;

    @NotNull(message = "Miejsce jest wymagane")
    private Long placeId;

    private String placeName; // <-- bez walidacji

    @NotBlank(message = "Status jest wymagany")
    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalTime getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(LocalTime plannedTime) {
        this.plannedTime = plannedTime;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}