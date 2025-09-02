package com.example.ioproject.place.controller;

import com.example.ioproject.place.dto.request.PlaceRequest;
import com.example.ioproject.place.dto.response.PlaceResponse;
import com.example.ioproject.place.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<PlaceResponse> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{id}")
    public PlaceResponse getPlace(@PathVariable Long id) {
        return placeService.getPlace(id);
    }

    @PostMapping
    public PlaceResponse addPlace(@RequestBody @Valid PlaceRequest request) {
        return placeService.addPlace(request);
    }

    @PutMapping("/{id}")
    public PlaceResponse updatePlace(@PathVariable Long id, @RequestBody @Valid PlaceRequest request) {
        return placeService.updatePlace(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }
}
