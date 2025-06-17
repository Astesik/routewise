// services/PlaceService.java
package com.example.ioproject.services;

import com.example.ioproject.models.Place;
import com.example.ioproject.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Optional<Place> getPlace(Long id) {
        return placeRepository.findById(id);
    }

    public Place addPlace(Place place) {
        return placeRepository.save(place);
    }

    public Place updatePlace(Long id, Place place) {
        place.setId(id);
        return placeRepository.save(place);
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}
