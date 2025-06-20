package com.example.ioproject.place.service;

import com.example.ioproject.place.dto.request.PlaceRequest;
import com.example.ioproject.place.dto.response.PlaceResponse;
import com.example.ioproject.place.exception.PlaceNotFoundException;
import com.example.ioproject.place.model.Place;
import com.example.ioproject.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<PlaceResponse> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PlaceResponse getPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException("Place with id " + id + " not found"));
        return mapToResponse(place);
    }

    public PlaceResponse addPlace(PlaceRequest request) {
        Place place = new Place();
        place.setName(request.getName());
        Place saved = placeRepository.save(place);
        return mapToResponse(saved);
    }

    public PlaceResponse updatePlace(Long id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException("Place with id " + id + " not found"));
        place.setName(request.getName());
        Place saved = placeRepository.save(place);
        return mapToResponse(saved);
    }

    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new PlaceNotFoundException("Place with id " + id + " not found");
        }
        placeRepository.deleteById(id);
    }

    private PlaceResponse mapToResponse(Place place) {
        return new PlaceResponse(place.getId(), place.getName());
    }
}
