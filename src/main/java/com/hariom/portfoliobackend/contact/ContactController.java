package com.hariom.portfoliobackend.contact;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * CONTROLLER — the HTTP layer.
 *
 * Flow of a POST /api/contact request:
 *   Tomcat -> DispatcherServlet -> this method (JSON already mapped to
 *   ContactRequest by Jackson, validated by @Valid) -> ContactService
 *   -> MySQL -> 201 Created with a JSON body.
 *
 * 201 means "created a resource". 200 would also work, but 201 is the
 * precise answer for a successful POST that stored something.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactMessageResponse> submit(@Valid @RequestBody ContactRequest request) {
        ContactMessageResponse saved = contactService.save(request);

        // Location header points at the new resource (GET /api/contact/health here,
        // since there is no per-message GET endpoint — kept simple on purpose).
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .location(URI.create("/api/contact/health"))
            .body(saved);
    }

    /**
     * Lightweight liveness check for the contact module:
     * answers whether the app is up AND whether the table is reachable.
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "portfolio-backend",
            "messagesStored", contactService.count(),
            "checkedAt", Instant.now().toString()
        );
    }
}
