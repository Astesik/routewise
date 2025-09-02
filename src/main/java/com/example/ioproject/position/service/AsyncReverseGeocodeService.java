package com.example.ioproject.position.service;

import com.example.ioproject.position.model.Position;
import com.example.ioproject.position.repository.PositionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncReverseGeocodeService {

    private final GeoLocationService geoLocationService;
    private final PositionRepository positionRepository;

    public AsyncReverseGeocodeService(GeoLocationService geoLocationService, PositionRepository positionRepository) {
        this.geoLocationService = geoLocationService;
        this.positionRepository = positionRepository;
    }

    @Async
    public void reverseGeocodeAndUpdate(Position pos) {
        try {
            String country = geoLocationService.getCountryCode(pos.getLatitude(), pos.getLongitude());
            if (country != null) {
                pos.setCountryCode(country);
                positionRepository.save(pos);
//                System.out.println("Ustawiono kraj " + country + " dla pojazdu " + pos.getDeviceId());
            }
        } catch (Exception ex) {
            System.err.println("Błąd reverse geocode dla " + pos.getDeviceId() + ": " + ex.getMessage());
        }
    }
}