// models/Place.java
package com.example.ioproject.place.model;

import jakarta.persistence.*;

@Entity
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    // Domyślny konstruktor (wymagany przez JPA)
    public Place() {}

    // Konstruktor pomocniczy
    public Place(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
