package com.example.ioproject.dto;

import java.util.List;

public class VehicleGroupCreateDTO {
    private String name;
    private List<Long> vehicleIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getVehicleIds() { return vehicleIds; }
    public void setVehicleIds(List<Long> vehicleIds) { this.vehicleIds = vehicleIds; }
}
