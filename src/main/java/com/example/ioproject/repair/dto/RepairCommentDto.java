package com.example.ioproject.repair.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class RepairCommentDto {
    private Long id;
    private Long repairId;

    // informacje o autorze zwracane klientowi
    private Long authorUserId;
    private String authorUsername;
    private String authorFullName; // opcjonalnie: "Imię Nazwisko"

    @NotBlank(message = "Pole 'text' jest wymagane")
    private String text;

    private Instant createdAt;

    public RepairCommentDto() {
    }

    // --- getters / setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepairId() {
        return repairId;
    }

    public void setRepairId(Long repairId) {
        this.repairId = repairId;
    }

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
