// controllers/PlaceController.java
package com.example.ioproject.controllers;

import com.example.ioproject.models.Place;
import com.example.ioproject.services.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("/get")
    public List<Place> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{id}")
    public Place getPlace(@PathVariable Long id) {
        return placeService.getPlace(id).orElse(null);
    }

    @PostMapping
    public Place addPlace(@RequestBody Place place) {
        return placeService.addPlace(place);
    }

    @PutMapping("/{id}")
    public Place updatePlace(@PathVariable Long id, @RequestBody Place place) {
        return placeService.updatePlace(id, place);
    }

    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
    }
}