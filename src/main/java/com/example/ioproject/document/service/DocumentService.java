package com.example.ioproject.document.service;

import com.example.ioproject.document.dto.DocumentDto;
import com.example.ioproject.document.dto.MissingDocumentDto;
import com.example.ioproject.document.model.Document;
import com.example.ioproject.document.model.DocumentType;
import com.example.ioproject.document.repository.DocumentRepository;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;

    private final Path rootPath = Paths.get("uploads/documents");

    public DocumentService(DocumentRepository documentRepository, VehicleRepository vehicleRepository) {
        this.documentRepository = documentRepository;
        this.vehicleRepository = vehicleRepository;

        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder!", e);
        }
    }

    public DocumentDto saveDocument(Long vehicleId, MultipartFile file, DocumentType type) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Usuwamy spacje/symbole z rejestracji (np. "TK12345")
        String licensePlateSafe = vehicle.getLicensePlate().replaceAll("[^A-Za-z0-9]", "_");

        Path vehicleDir = rootPath.resolve(licensePlateSafe);
        Files.createDirectories(vehicleDir);

        String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = vehicleDir.resolve(uniqueName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setOriginalFilename(file.getOriginalFilename());
        doc.setFilename(uniqueName);
        doc.setContentType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setPath(filePath.toString());
        doc.setType(type);
        doc.setVehicle(vehicle);

        documentRepository.save(doc);

        DocumentDto dto = new DocumentDto();
        dto.setId(doc.getId());
        dto.setOriginalFilename(doc.getOriginalFilename());
        dto.setContentType(doc.getContentType());
        dto.setSize(doc.getSize());
        dto.setDownloadUrl("/api/documents/download/" + doc.getId());
        dto.setType(doc.getType());

        return dto;
    }

    public List<Document> getDocumentsByVehicle(Long vehicleId) {
        return documentRepository.findByVehicleId(vehicleId);
    }

    public void writeDocumentToResponse(Long id, HttpServletResponse response) throws IOException {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        response.setContentType(doc.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getOriginalFilename() + "\"");
        Files.copy(Path.of(doc.getPath()), response.getOutputStream());
        response.flushBuffer();
    }

    public List<MissingDocumentDto> findMissingOrOutdatedDocuments() {
        List<MissingDocumentDto> result = new ArrayList<>();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle v : vehicles) {
            // --- REGISTRATION ---
            List<Document> regDocs = documentRepository.findByVehicleId(v.getId()).stream()
                    .filter(d -> d.getType() == DocumentType.REGISTRATION)
                    .sorted(Comparator.comparing(Document::getCreatedAt).reversed())
                    .toList();

            LocalDate techInspectionUntil = v.getTechnicalInspection();
            LocalDate techInspectionDone = techInspectionUntil != null ? techInspectionUntil.minusYears(1) : null;

            if (regDocs.isEmpty()) {
                // Brak dowodu rejestracyjnego
                MissingDocumentDto dto = new MissingDocumentDto();
                dto.setVehicleId(v.getId());
                dto.setLicensePlate(v.getLicensePlate());
                dto.setDocumentType("REGISTRATION");
                dto.setReason("missing");
                dto.setTechnicalInspection(techInspectionUntil);
                dto.setTechnicalInspectionDone(techInspectionDone);
                result.add(dto);
            } else if (techInspectionDone != null) {
                // Sprawdź czy najnowszy dowód nie jest starszy niż dzień wykonania przeglądu
                Document newestReg = regDocs.get(0);
                if (newestReg.getCreatedAt().toLocalDate().isBefore(techInspectionDone)) {
                    MissingDocumentDto dto = new MissingDocumentDto();
                    dto.setVehicleId(v.getId());
                    dto.setLicensePlate(v.getLicensePlate());
                    dto.setDocumentType("REGISTRATION");
                    dto.setReason("outdated");
                    dto.setDocumentCreatedAt(newestReg.getCreatedAt());
                    dto.setTechnicalInspection(techInspectionUntil);
                    dto.setTechnicalInspectionDone(techInspectionDone);
                    result.add(dto);
                }
            }

            // --- EMISSION_CERTIFICATE ---
            List<Document> emissionDocs = documentRepository.findByVehicleId(v.getId()).stream()
                    .filter(d -> d.getType() == DocumentType.EMISSION_CERTIFICATE)
                    .toList();
            if (emissionDocs.isEmpty()) {
                MissingDocumentDto dto = new MissingDocumentDto();
                dto.setVehicleId(v.getId());
                dto.setLicensePlate(v.getLicensePlate());
                dto.setDocumentType("EMISSION_CERTIFICATE");
                dto.setReason("missing");
                result.add(dto);
            }
        }
        return result;
    }

    public Optional<Document> getById(Long id) {
        return documentRepository.findById(id);
    }
}
