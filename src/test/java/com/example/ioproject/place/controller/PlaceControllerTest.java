package com.example.ioproject.place.controller;

import com.example.ioproject.place.dto.response.PlaceResponse;
import com.example.ioproject.place.service.PlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PlaceControllerTest.MockConfig.class)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceService placeService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public PlaceService placeService() {
            return Mockito.mock(PlaceService.class);
        }
    }

    @BeforeEach
    void setupMocks() {
        Mockito.reset(placeService);
    }

    @Test
    void getAllPlaces_returnsOk() throws Exception {
        Mockito.when(placeService.getAllPlaces())
                .thenReturn(List.of(new PlaceResponse(1L, "Test Place")));

        mockMvc.perform(get("/api/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Place"));
    }

    @Test
    void getPlace_returnsOk() throws Exception {
        Mockito.when(placeService.getPlace(1L))
                .thenReturn(new PlaceResponse(1L, "Test Place"));

        mockMvc.perform(get("/api/places/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Place"));
    }

    @Test
    void addPlace_returnsOk() throws Exception {
        Mockito.when(placeService.addPlace(any()))
                .thenReturn(new PlaceResponse(1L, "New Place"));

        mockMvc.perform(post("/api/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Place\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Place"));
    }

    @Test
    void updatePlace_returnsOk() throws Exception {
        Mockito.when(placeService.updatePlace(eq(1L), any()))
                .thenReturn(new PlaceResponse(1L, "Updated Place"));

        mockMvc.perform(put("/api/places/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Place\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Place"));
    }

    @Test
    void deletePlace_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/places/1"))
                .andExpect(status().isNoContent());
    }
}
