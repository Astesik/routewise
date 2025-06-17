// repositories/RepairRepository.java
package com.example.ioproject.repair.repository;

import com.example.ioproject.repair.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByVehicleId(Long vehicleId);
    List<Repair> findByStatus(String status);
}