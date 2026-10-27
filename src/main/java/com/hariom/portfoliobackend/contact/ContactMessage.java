package com.hariom.portfoliobackend.contact;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * ENTITY — one row of the contact_messages MySQL table.
 *
 * Layer rule: the entity mirrors the DATABASE, not the API.
 * What the outside world sends/receives is shaped by the DTOs
 * (ContactRequest, ContactMessageResponse) instead.
 *
 * No Lombok on purpose: read the getters/setters once and you will
 * never have to guess what Lombok generated.
 */
@Entity
@Table(
    name = "contact_messages",
    indexes = @Index(name = "idx_contact_created_at", columnList = "created_at")
)
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL AUTO_INCREMENT
    private Long id;

    @Column(name = "sender_name", nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = false, length = 180)
    private String email;

    /** MySQL VARCHAR(5000) — the form textarea is capped at the same length. */
    @Column(nullable = false, length = 5000)
    private String message;

    /** Set once, on insert — never updated. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** JPA always needs a no-arg constructor (public or protected). */
    public ContactMessage() {
    }

    public ContactMessage(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    /** Lifecycle hook: runs right before the INSERT is written. */
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMessage(String message) { this.message = message; }
}
