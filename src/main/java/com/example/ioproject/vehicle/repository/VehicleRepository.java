package com.example.ioproject.vehicle.repository;

import com.example.ioproject.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByType(String type);

    Optional<Vehicle> findByDevice_SerialNumber(String serialNumber);

    List<Vehicle> findByTypeIgnoreCase(String type);
}
