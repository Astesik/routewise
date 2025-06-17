// models/Repair.java
package com.example.ioproject.repair.model;

import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.place.model.Place;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "repairs")
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate plannedDate;
    private LocalTime plannedTime;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }

    public LocalTime getPlannedTime() { return plannedTime; }
    public void setPlannedTime(LocalTime plannedTime) { this.plannedTime = plannedTime; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
