package com.example.ioproject.repair.model;

import com.example.ioproject.auth.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "repair_comment")
public class RepairComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> repairs.id z kasowaniem w bazie
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repair_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_repair_comment_repair"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Repair repair;

    // autor (bez kaskady kasowania użytkownika)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id",
            foreignKey = @ForeignKey(name = "fk_repair_comment_author"))
    private User author;

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Repair getRepair() { return repair; }
    public void setRepair(Repair repair) { this.repair = repair; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
