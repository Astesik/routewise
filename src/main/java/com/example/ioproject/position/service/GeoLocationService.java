package com.example.ioproject.position.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeoLocationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeoLocationService() {
        this.restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", "routewise-fleet/1.0 (kontakt@okbruk.pl)");
            return execution.execute(request, body);
        });
    }

    public synchronized String getCountryCode(double lat, double lon) {
        try {
            Thread.sleep(1250); // 1.25 sek throttlingu, żeby nie przekroczyć limitu
            String url = UriComponentsBuilder.fromHttpUrl("https://nominatim.openstreetmap.org/reverse")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("format", "json")
                    .queryParam("zoom", 3)
                    .queryParam("addressdetails", 1)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
//            System.out.println(response);
            JsonNode obj = objectMapper.readTree(response);
            JsonNode address = obj.get("address");
            if (address != null && address.get("country_code") != null) {
                return address.get("country_code").asText().toUpperCase();
            }
        } catch (Exception e) {
            System.err.println("Błąd reverse geocode: " + e.getMessage());
        }
        return "N/A";
    }
}
