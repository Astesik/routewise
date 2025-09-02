package com.example.ioproject.place.dto.request;

import jakarta.validation.constraints.*;

public class PlaceRequest {

    @NotBlank
    @Size(max = 160)
    private String name;

    @DecimalMin(value = "-180.0", inclusive = true, message = "longitude musi być w zakresie [-180, 180]")
    @DecimalMax(value = "180.0", inclusive = true, message = "longitude musi być w zakresie [-180, 180]")
    private Double longitude; // opcjonalne

    @DecimalMin(value = "-90.0", inclusive = true, message = "latitude musi być w zakresie [-90, 90]")
    @DecimalMax(value = "90.0", inclusive = true, message = "latitude musi być w zakresie [-90, 90]")
    private Double latitude; // opcjonalne

    @Size(max = 32)
    @Pattern(regexp = "^[0-9+()\\-\\s]{0,32}$", message = "phone ma nieprawidłowy format")
    private String phone; // opcjonalne

    @Email(message = "email ma nieprawidłowy format")
    @Size(max = 128)
    private String email; // opcjonalne

    private String description; // opcjonalne

    @Min(value = 0, message = "radius nie może być ujemny")
    private Integer radius; // w metrach, opcjonalny

    // --- getters/setters ---
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
