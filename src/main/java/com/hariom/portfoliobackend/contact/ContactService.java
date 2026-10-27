package com.hariom.portfoliobackend.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * SERVICE — the business-logic layer between controller and repository.
 *
 * Today save() is one line, so why a separate class?
 * 1. The controller only does HTTP (status codes, JSON). It never talks
 *    to the repository directly.
 * 2. Tomorrow's features — email notification, spam filtering, rate
 *    limiting — all land HERE, and the controller stays unchanged.
 * 3. It becomes easy to unit test without any HTTP involved.
 *
 * @Service marks this class as a Spring-managed bean; Spring injects
 * the repository through the constructor (single constructor = no
 * @Autowired needed).
 */
@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Persist one contact message and return what the API should answer with.
     * The transaction (INSERT + commit) is handled by SimpleJpaRepository#save.
     */
    public ContactMessageResponse save(ContactRequest request) {
        // Normalise before storing: trim stray spaces so stored data stays clean.
        ContactMessage saved = contactRepository.save(new ContactMessage(
            request.name().trim(),
            request.email().trim().toLowerCase(),
            request.message().trim()
        ));

        log.info("Contact message #{} saved from {} <{}>", saved.getId(), saved.getName(), saved.getEmail());
        return ContactMessageResponse.from(saved);
    }

    /** Cheap table count — wired to GET /api/contact/health for quick checks. */
    public long count() {
        return contactRepository.count();
    }
}
