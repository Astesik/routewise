// repositories/PlaceRepository.java
package com.example.ioproject.place.repository;

import com.example.ioproject.place.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
