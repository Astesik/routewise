package com.example.ioproject.place.dto.response;

public class PlaceResponse {
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;
    private String phone;
    private String email;
    private String description;
    private Integer radius; // metry

    public PlaceResponse(
            Long id,
            String name,
            Double longitude,
            Double latitude,
            String phone,
            String email,
            String description,
            Integer radius
    ) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.email = email;
        this.description = description;
        this.radius = radius;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getLongitude() { return longitude; }
    public Double getLatitude() { return latitude; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
    public Integer getRadius() { return radius; }
}
