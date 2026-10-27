package com.hariom.portfoliobackend.contact;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;

/**
 * Unit tests for the ContactRequest validation rules.
 * Plain JUnit — no Spring context and no MySQL needed, so `mvn test`
 * passes on any machine.
 */
class ContactRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private Set<String> violationsOf(ContactRequest request) {
        return validator.validate(request).stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.toSet());
    }

    @Test
    void validRequestHasNoViolations() {
        var violations = violationsOf(
            new ContactRequest("Hariom Verma", "visitor@example.com", "Hello, nice portfolio!"));
        assertTrue(violations.isEmpty(), () -> "expected no violations but got: " + violations);
    }

    @Test
    void blankFieldsAreRejected() {
        var violations = violationsOf(new ContactRequest("", " ", null));

        assertTrue(violations.stream().anyMatch(v -> v.startsWith("name")),
            () -> "expected a name violation but got: " + violations);
        assertTrue(violations.stream().anyMatch(v -> v.startsWith("message")),
            () -> "expected a message violation but got: " + violations);
        // A whitespace-only email trips BOTH @NotBlank and @Email — the
        // validator checks every constraint, it does not stop at the first.
        assertEquals(2, violations.stream().filter(v -> v.startsWith("email")).count(),
            () -> "expected two email violations but got: " + violations);
    }

    @Test
    void malformedEmailIsRejected() {
        var violations = violationsOf(
            new ContactRequest("Hariom", "not-an-email", "Hi"));
        assertTrue(violations.stream().anyMatch(v -> v.startsWith("email")),
            () -> "expected an email violation but got: " + violations);
    }

    @Test
    void overlongMessageIsRejected() {
        var violations = violationsOf(
            new ContactRequest("Hariom", "visitor@example.com", "x".repeat(5001)));
        assertTrue(violations.stream().anyMatch(v -> v.startsWith("message")),
            () -> "expected a message-size violation but got: " + violations);
    }
}
