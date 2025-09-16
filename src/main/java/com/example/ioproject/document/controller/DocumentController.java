package com.example.ioproject.document.controller;

import com.example.ioproject.document.dto.DocumentDto;
import com.example.ioproject.document.dto.MissingDocumentDto;
import com.example.ioproject.document.model.Document;
import com.example.ioproject.document.model.DocumentType;
import com.example.ioproject.document.service.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentDto> upload(
            @PathVariable Long vehicleId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentType type,
            @RequestParam(value = "validUntil", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validUntil
    ) throws IOException {
        return ResponseEntity.ok(documentService.saveDocument(vehicleId, file, type, validUntil));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Document>> getDocuments(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(documentService.getDocumentsByVehicle(vehicleId));
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        documentService.writeDocumentToResponse(id, response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todo")
    public ResponseEntity<List<MissingDocumentDto>> getMissingOrOutdatedDocuments() {
        return ResponseEntity.ok(documentService.findMissingOrOutdatedDocuments());
    }
}