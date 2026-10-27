package com.hariom.portfoliobackend.contact;

import java.time.LocalDateTime;

/**
 * OUTGOING DTO — what the API answers with after saving.
 *
 * Deliberately NOT the entity: it hides the message body (the sender
 * typed it; nobody else needs to read it back through the API) and
 * renames createdAt to receivedAt so the JSON reads nicely.
 *
 * Entity = database shape. DTO = API shape. Keeping them separate
 * means you can change either without breaking the other.
 */
public record ContactMessageResponse(
    Long id,
    String name,
    String email,
    LocalDateTime receivedAt
) {
    public static ContactMessageResponse from(ContactMessage m) {
        return new ContactMessageResponse(m.getId(), m.getName(), m.getEmail(), m.getCreatedAt());
    }
}
