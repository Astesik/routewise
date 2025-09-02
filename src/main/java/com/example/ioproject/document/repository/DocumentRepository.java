package com.example.ioproject.document.repository;

import com.example.ioproject.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByVehicleId(Long vehicleId);
}
