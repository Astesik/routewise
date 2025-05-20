package com.example.ioproject.controllers;

import com.example.ioproject.models.Position;
import com.example.ioproject.dto.PositionDetailsProjection;
import com.example.ioproject.services.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/get")
    public List<PositionDetailsProjection> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/countries")
    public Map<String, Long> getVehicleCountByCountry() {
        return positionService.getVehicleCountByCountry();
    }

    @GetMapping("/low-fuel")
    public List<PositionDetailsProjection> getLowFuelVehicles() {
        return positionService.getLowFuelVehicles();
    }

    @GetMapping("/by-country")
    public Map<String, List<PositionDetailsProjection>> getPositionsGroupedByCountry() {
        return positionService.getPositionsGroupedByCountry();
    }
}
