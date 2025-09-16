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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;

    /** Główny katalog uploadów */
    private final Path rootPath = Paths.get("uploads/documents");

    public DocumentService(DocumentRepository documentRepository,
                           VehicleRepository vehicleRepository) {
        this.documentRepository = documentRepository;
        this.vehicleRepository = vehicleRepository;

        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder!", e);
        }
    }

    /* =========================================================
       ZAPIS DOKUMENTU
       ========================================================= */

    /** Nowy wariant: z datą ważności (np. dla REGISTRATION) */
    public DocumentDto saveDocument(Long vehicleId,
                                    MultipartFile file,
                                    DocumentType type,
                                    LocalDate validUntil) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        String licensePlate = Optional.ofNullable(vehicle.getLicensePlate())
                .map(lp -> lp.replaceAll("[^A-Za-z0-9]", "_"))
                .filter(s -> !s.isBlank())
                .orElse("vehicle_" + vehicle.getId());

        Path vehicleDir = rootPath.resolve(licensePlate);
        Files.createDirectories(vehicleDir);

        String original = sanitizeFilename(file.getOriginalFilename());
        String uniqueName = UUID.randomUUID() + "_" + original;
        Path filePath = vehicleDir.resolve(uniqueName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Document doc = new Document();
        doc.setOriginalFilename(original);
        doc.setFilename(uniqueName);
        doc.setContentType(
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        );
        doc.setSize(file.getSize());
        doc.setPath(filePath.toString());
        doc.setType(type);
        doc.setVehicle(vehicle);
        doc.setValidUntil(validUntil); // <<< data ważności

        Document saved = documentRepository.save(doc);
        return toDto(saved);
    }

    /** Stary wariant – dla kompatybilności (bez daty ważności) */
    public DocumentDto saveDocument(Long vehicleId,
                                    MultipartFile file,
                                    DocumentType type) throws IOException {
        return saveDocument(vehicleId, file, type, null);
    }

    private static String sanitizeFilename(String name) {
        if (!StringUtils.hasText(name)) return "file";
        // usuń separatory katalogów i znaki sterujące
        String cleaned = name.replace("\\", "_").replace("/", "_").replaceAll("\\p{Cntrl}", "");
        // skróć ekstremalnie długie nazwy
        if (cleaned.length() > 150) {
            String ext = "";
            int dot = cleaned.lastIndexOf('.');
            if (dot > 0 && dot < cleaned.length() - 1) {
                ext = cleaned.substring(dot);
                cleaned = cleaned.substring(0, Math.min(150 - ext.length(), dot));
                cleaned = cleaned + ext;
            } else {
                cleaned = cleaned.substring(0, 150);
            }
        }
        return cleaned;
    }

    private DocumentDto toDto(Document d) {
        DocumentDto dto = new DocumentDto();
        dto.setId(d.getId());
        dto.setOriginalFilename(d.getOriginalFilename());
        dto.setContentType(d.getContentType());
        dto.setSize(d.getSize());
        dto.setDownloadUrl("/api/documents/download/" + d.getId());
        dto.setType(d.getType());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setValidUntil(d.getValidUntil()); // <<< do frontu
        return dto;
    }

    /* =========================================================
       ODCZYT LISTY I POJEDYNCZYCH PLIKÓW
       ========================================================= */

    /** Zwraca surowe encje – tak jak oczekuje aktualny kontroler */
    public List<Document> getDocumentsByVehicle(Long vehicleId) {
        List<Document> list = documentRepository.findByVehicleId(vehicleId);
        return list.stream()
                .sorted(Comparator.comparing(Document::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<DocumentDto> getDocumentDtosByVehicle(Long vehicleId) {
        return getDocumentsByVehicle(vehicleId).stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<Document> getById(Long id) {
        return documentRepository.findById(id);
    }

    /** Bezpieczne streamowanie pliku do odpowiedzi HTTP */
    public void writeDocumentToResponse(Long id, HttpServletResponse response) throws IOException {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Path path = Paths.get(doc.getPath());
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found on disk");
        }

        String ct = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        response.setContentType(ct);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getOriginalFilename() + "\"");
        response.setContentLengthLong(Files.size(path));

        try (InputStream in = Files.newInputStream(path)) {
            in.transferTo(response.getOutputStream());
        }
        response.flushBuffer();
    }

    /* =========================================================
       USUWANIE
       ========================================================= */

    public void deleteDocument(Long id) {
        documentRepository.findById(id).ifPresent(doc -> {
            try {
                Path p = Paths.get(doc.getPath());
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    // loguj e w realnym kodzie, nie przerywaj
                }
            } finally {
                documentRepository.deleteById(id);
            }
        });
    }

    /* =========================================================
       TODO: BRAKUJĄCE/WYGASŁE DOKUMENTY
       ========================================================= */

    /**
     * Wyznacza „TODO” per pojazd:
     *  - REGISTRATION: brak dokumentu -> "missing"
     *                  istnieje, ale validUntil < today -> "outdated"
     *                  validUntil == null -> "outdated" (brak daty – traktujemy jak do uzupełnienia)
     *  - EMISSION_CERTIFICATE: brak -> "missing" WYŁĄCZNIE dla pojazdów wymagających tego dokumentu
     *                           (np. ciągniki); naczepy (trailer) pomijamy.
     *
     *  Fallback (gdy brak validUntil): jeśli masz daty przeglądu na pojeździe,
     *  można bazować na createdAt vs. data wykonania przeglądu.
     */
    public List<MissingDocumentDto> findMissingOrOutdatedDocuments() {
        List<MissingDocumentDto> result = new ArrayList<>();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Vehicle v : vehicles) {
            List<Document> allDocs = documentRepository.findByVehicleId(v.getId());

            // ===== REGISTRATION =====
            Optional<Document> newestRegOpt = allDocs.stream()
                    .filter(d -> d.getType() == DocumentType.REGISTRATION)
                    .max(Comparator.comparing(Document::getCreatedAt));

            if (newestRegOpt.isEmpty()) {
                // Brak dowodu rejestracyjnego
                MissingDocumentDto dto = new MissingDocumentDto();
                dto.setVehicleId(v.getId());
                dto.setLicensePlate(v.getLicensePlate());
                dto.setDocumentType("REGISTRATION");
                dto.setReason("missing");
                dto.setTechnicalInspection(v.getTechnicalInspection());
                dto.setTechnicalInspectionDone(
                        v.getTechnicalInspection() != null ? v.getTechnicalInspection().minusYears(1) : null
                );
                result.add(dto);
            } else {
                Document newestReg = newestRegOpt.get();
                LocalDate valid = newestReg.getValidUntil();

                boolean outdated;
                if (valid != null) {
                    outdated = valid.isBefore(today);
                } else {
                    // brak daty ważności -> do uzupełnienia
                    outdated = true;

                    // opcjonalny fallback do starej reguły, jeśli masz daty przeglądu na pojeździe
                    LocalDate techUntil = v.getTechnicalInspection();
                    LocalDate techDone = techUntil != null ? techUntil.minusYears(1) : null;
                    if (techDone != null && newestReg.getCreatedAt() != null) {
                        // jeśli chcesz — możesz tu dodatkowo sterować 'outdated' na podstawie createdAt vs techDone
                        // na razie pozostawiamy jako 'true' by wymusić uzupełnienie validUntil
                    }
                }

                if (outdated) {
                    MissingDocumentDto dto = new MissingDocumentDto();
                    dto.setVehicleId(v.getId());
                    dto.setLicensePlate(v.getLicensePlate());
                    dto.setDocumentType("REGISTRATION");
                    dto.setReason("outdated");
                    dto.setDocumentCreatedAt(newestReg.getCreatedAt());
                    // W Twoim DTO pole technicalInspection wykorzystujemy jako „validUntil” dla REGISTRATION
                    dto.setTechnicalInspection(valid);
                    dto.setTechnicalInspectionDone(
                            v.getTechnicalInspection() != null ? v.getTechnicalInspection().minusYears(1) : null
                    );
                    result.add(dto);
                }
            }

            // ===== EMISSION_CERTIFICATE — tylko dla pojazdów wymagających (nie naczepy) =====
            if (requiresEmissionCertificate(v)) {
                boolean hasEmission = allDocs.stream()
                        .anyMatch(d -> d.getType() == DocumentType.EMISSION_CERTIFICATE);
                if (!hasEmission) {
                    MissingDocumentDto dto = new MissingDocumentDto();
                    dto.setVehicleId(v.getId());
                    dto.setLicensePlate(v.getLicensePlate());
                    dto.setDocumentType("EMISSION_CERTIFICATE");
                    dto.setReason("missing");
                    result.add(dto);
                }
            }
        }
        return result;
    }

    /** Emisja wymagana dla wszystkiego poza naczepą (TRAILER). */
    private boolean requiresEmissionCertificate(Vehicle v) {
        if (v == null) return true; // zachowawczo
        Object t = null;
        try {
            // jeżeli masz enum: v.getType() -> TRUCK/TRAILER (toString() wystarczy)
            t = v.getType();
        } catch (Exception ignored) {}
        String type = t == null ? "" : t.toString();
        type = type.trim().toLowerCase();
        // dopasuj do swoich wartości: 'truck' / 'trailer' lub enum VehicleType.TRAILER
        return !type.equals("trailer");
    }
}
