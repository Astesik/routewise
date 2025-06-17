package com.example.ioproject.vehicle.model;

import com.example.ioproject.device.model.Device;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic info
    private String licensePlate; // Nr rejestracyjny
    private String vin; // VIN
    private LocalDate firstRegistration; // I Rejestracja
    private int productionYear; // Rok produkcji
    private String make; // Marka
    private String euroClass; // Klasa EURO
    private String type; // TRUCK, TRAILER, CAR (ciągnik, naczepa, osobówka)
    private String ownership; // Własność (company name/owner)
    private String ownershipUntil; // doKiedyWłasność (może być też LocalDate jeśli zawsze data)
    private LocalDate technicalInspection; // Przegląd
    private LocalDate tachographInspection; // Tachograf
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", unique = true)
    private Device device;

    // Add any other fields you need...

    // --- Gettery i settery poniżej (wygeneruj w IDE) ---
    // (pokażę kilka dla przykładu)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public LocalDate getFirstRegistration() { return firstRegistration; }
    public void setFirstRegistration(LocalDate firstRegistration) { this.firstRegistration = firstRegistration; }

    public int getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getEuroClass() { return euroClass; }
    public void setEuroClass(String euroClass) { this.euroClass = euroClass; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOwnership() { return ownership; }
    public void setOwnership(String ownership) { this.ownership = ownership; }

    public String getOwnershipUntil() { return ownershipUntil; }
    public void setOwnershipUntil(String ownershipUntil) { this.ownershipUntil = ownershipUntil; }

    public LocalDate getTechnicalInspection() { return technicalInspection; }
    public void setTechnicalInspection(LocalDate technicalInspection) { this.technicalInspection = technicalInspection; }

    public LocalDate getTachographInspection() { return tachographInspection; }
    public void setTachographInspection(LocalDate tachographInspection) { this.tachographInspection = tachographInspection; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    // Dodaj pozostałe gettery/settery...
}
