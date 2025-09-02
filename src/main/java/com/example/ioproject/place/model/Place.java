package com.example.ioproject.place.model;

import jakarta.persistence.*;

@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 160)
    private String name;

    // współrzędne (opcjonalne)
    @Column(name = "longitude", columnDefinition = "double precision")
    private Double longitude;

    @Column(name = "latitude", columnDefinition = "double precision")
    private Double latitude;

    // kontakt (opcjonalny)
    @Column(length = 32)
    private String phone;

    @Column(length = 128)
    private String email;

    // opis (opcjonalny)
    @Column(columnDefinition = "text")
    private String description;

    // promień w metrach (opcjonalny)
    @Column(name = "radius_meters")
    private Integer radius;

    public Place() {}

    public Place(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }
}
