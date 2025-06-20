package com.example.ioproject.place.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PlaceRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
