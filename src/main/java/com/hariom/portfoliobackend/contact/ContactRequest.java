package com.hariom.portfoliobackend.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) — the shape of ONE contact-form submission
 * coming IN from the React app.
 *
 * Why a record, not a class? A record is an immutable data carrier:
 * constructor, getters (name(), email()...) and equals/hashCode are
 * generated. Perfect for request payloads that nobody should edit.
 *
 * The @Valid annotations are enforced by Spring before the controller
 * body runs — bad input never reaches the service layer.
 */
public record ContactRequest(

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be at most 120 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 180, message = "Email must be at most 180 characters")
    String email,

    @NotBlank(message = "Message is required")
    @Size(max = 5000, message = "Message must be at most 5000 characters")
    String message
) {
}
