package com.example.ioproject.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicle_groups")
public class VehicleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // nazwa grupy

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicle_group_vehicles",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private Set<Vehicle> vehicles = new HashSet<>();

    // --- Gettery i settery ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(Set<Vehicle> vehicles) { this.vehicles = vehicles; }
}
