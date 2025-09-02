package com.example.ioproject.document.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MissingDocumentDto {
    private Long vehicleId;
    private String licensePlate;
    private String documentType; // "REGISTRATION" lub "EMISSION_CERTIFICATE"
    private String reason; // "missing" lub "outdated"
    private LocalDateTime documentCreatedAt;
    private LocalDate technicalInspection;      // data DO KIEDY ważny przegląd
    private LocalDate technicalInspectionDone;  // data wykonania przeglądu

    // Gettery i settery
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getDocumentCreatedAt() { return documentCreatedAt; }
    public void setDocumentCreatedAt(LocalDateTime documentCreatedAt) { this.documentCreatedAt = documentCreatedAt; }
    public LocalDate getTechnicalInspection() { return technicalInspection; }
    public void setTechnicalInspection(LocalDate technicalInspection) { this.technicalInspection = technicalInspection; }
    public LocalDate getTechnicalInspectionDone() { return technicalInspectionDone; }
    public void setTechnicalInspectionDone(LocalDate technicalInspectionDone) { this.technicalInspectionDone = technicalInspectionDone; }
}