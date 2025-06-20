package com.example.ioproject.place.dto.response;

public class PlaceResponse {
    private Long id;
    private String name;

    public PlaceResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
