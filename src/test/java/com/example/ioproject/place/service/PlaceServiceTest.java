package com.example.ioproject.place.service;

import com.example.ioproject.place.dto.request.PlaceRequest;
import com.example.ioproject.place.dto.response.PlaceResponse;
import com.example.ioproject.place.exception.PlaceNotFoundException;
import com.example.ioproject.place.model.Place;
import com.example.ioproject.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void getAllPlaces_returnsList() {
        when(placeRepository.findAll()).thenReturn(List.of(new Place(1L, "Place A")));

        List<PlaceResponse> result = placeService.getAllPlaces();

        assertEquals(1, result.size());
        assertEquals("Place A", result.get(0).getName());
    }

    @Test
    void getPlace_existingId_returnsResponse() {
        Place place = new Place(1L, "Place A");
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        PlaceResponse result = placeService.getPlace(1L);

        assertEquals("Place A", result.getName());
    }

    @Test
    void getPlace_nonExistingId_throwsException() {
        when(placeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> placeService.getPlace(1L));
    }

    @Test
    void addPlace_savesAndReturnsResponse() {
        PlaceRequest request = new PlaceRequest();
        request.setName("New Place");

        Place saved = new Place(1L, "New Place");
        when(placeRepository.save(any())).thenReturn(saved);

        PlaceResponse result = placeService.addPlace(request);

        assertEquals("New Place", result.getName());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        verify(placeRepository).save(captor.capture());
        assertEquals("New Place", captor.getValue().getName());
    }

    @Test
    void updatePlace_existingId_updatesAndReturnsResponse() {
        Place existing = new Place(1L, "Old Place");
        when(placeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(placeRepository.save(any())).thenReturn(new Place(1L, "Updated Place"));

        PlaceRequest request = new PlaceRequest();
        request.setName("Updated Place");

        PlaceResponse result = placeService.updatePlace(1L, request);

        assertEquals("Updated Place", result.getName());
    }

    @Test
    void updatePlace_nonExistingId_throwsException() {
        when(placeRepository.findById(1L)).thenReturn(Optional.empty());

        PlaceRequest request = new PlaceRequest();
        request.setName("Update");

        assertThrows(PlaceNotFoundException.class, () -> placeService.updatePlace(1L, request));
    }

    @Test
    void deletePlace_existingId_deletes() {
        when(placeRepository.existsById(1L)).thenReturn(true);

        placeService.deletePlace(1L);

        verify(placeRepository).deleteById(1L);
    }

    @Test
    void deletePlace_nonExistingId_throwsException() {
        when(placeRepository.existsById(1L)).thenReturn(false);

        assertThrows(PlaceNotFoundException.class, () -> placeService.deletePlace(1L));
    }
}
