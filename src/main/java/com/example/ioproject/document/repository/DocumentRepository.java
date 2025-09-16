package com.example.ioproject.document.repository;

import com.example.ioproject.document.model.Document;
import com.example.ioproject.document.model.DocumentType;
import com.example.ioproject.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByVehicleId(Long vehicleId);
    List<Document> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);
    Optional<Document> findTopByVehicleIdAndTypeOrderByCreatedAtDesc(Long vehicleId, DocumentType type);
}
