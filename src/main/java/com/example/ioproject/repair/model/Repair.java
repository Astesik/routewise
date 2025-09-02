// src/main/java/com/example/ioproject/repair/model/Repair.java
package com.example.ioproject.repair.model;

import com.example.ioproject.place.model.Place;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.repair.dto.RepairItemDto;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repairs")
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // pojazd
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id",
            foreignKey = @ForeignKey(name = "fk_repair_vehicle"))
    private Vehicle vehicle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate plannedDate;
    private LocalTime plannedTime;

    @Column(length = 64)
    private String status;

    // miejsce
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id",
            foreignKey = @ForeignKey(name = "fk_repair_place"))
    private Place place;

    @Column(length = 128)
    private String createdBy;

    // komentarze
    @OneToMany(mappedBy = "repair", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RepairComment> comments = new ArrayList<>();

    // NOWE: JSONB z listą pozycji
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "items", columnDefinition = "jsonb", nullable = false)
    private List<RepairItemDto> items = new ArrayList<>();

    // --- helpers ---
    public void addComment(RepairComment c) { comments.add(c); c.setRepair(this); }
    public void removeComment(RepairComment c) { comments.remove(c); c.setRepair(null); }

    // --- getters/setters ---
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<RepairComment> getComments() { return comments; }
    public void setComments(List<RepairComment> comments) { this.comments = comments; }

    public List<RepairItemDto> getItems() { return items; }
    public void setItems(List<RepairItemDto> items) { this.items = items; }
}
