package com.hariom.portfoliobackend.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for the service layer. The repository is a Mockito mock,
 * so this test needs no database either.
 */
@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    void saveTrimsAndNormalisesInput() {
        // Whatever save() is called with, answer with that same entity
        // (a stand-in for what MySQL would hand back with an id filled in).
        when(contactRepository.save(any(ContactMessage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ContactMessageResponse response = contactService.save(
            new ContactRequest("  Hariom  ", "  Visitor@Example.COM  ", "  Hello there  "));

        // The response carries only API-safe fields (no message body).
        assertEquals("Hariom", response.name());
        assertEquals("visitor@example.com", response.email());

        // The message lives on the ENTITY that went to the database —
        // catch it with an argument matcher and assert the trimming there.
        verify(contactRepository).save(argThat(entity ->
            "Hariom".equals(entity.getName())
                && "visitor@example.com".equals(entity.getEmail())
                && "Hello there".equals(entity.getMessage())
        ));
        verify(contactRepository).save(any(ContactMessage.class));
    }
}
