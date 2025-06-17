// repositories/PlaceRepository.java
package com.example.ioproject.repository;

import com.example.ioproject.models.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
