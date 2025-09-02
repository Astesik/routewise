package com.example.ioproject.document.dto;

import com.example.ioproject.document.model.DocumentType;

import java.time.LocalDateTime;

public class DocumentDto {
    private Long id;
    private String originalFilename;
    private String contentType;
    private long size;
    private String downloadUrl;
    private DocumentType type;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }
}
