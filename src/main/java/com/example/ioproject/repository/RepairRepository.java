// repositories/RepairRepository.java
package com.example.ioproject.repository;

import com.example.ioproject.models.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByVehicleId(Long vehicleId);
    List<Repair> findByStatus(String status);
}