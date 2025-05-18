package com.example.ioproject.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GpsService {
    private final RestTemplate restTemplate;

    public GpsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getPositions() {
        String url = "https://logowanie.gpsonline.com.pl:443/atlas/okbruk/positionsextended?password=BodzentynBruk";
        return restTemplate.getForObject(url, Map.class);
    }
    public Map<String, Object> getDrivers() {
        String url = "https://logowanie.gpsonline.com.pl:443/atlas/okbruk/drivers?password=BodzentynBruk";
        return restTemplate.getForObject(url, Map.class);
    }
}
