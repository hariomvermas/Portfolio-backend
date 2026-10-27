package com.hariom.portfoliobackend.contact;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * REPOSITORY — the database talks through this interface.
 *
 * You write NO implementation. At startup Spring Data JPA generates one
 * from the interface (proxy pattern). save(), findById(), count() etc.
 * come free from JpaRepository.
 *
 * Need a custom query later? Just declare the method signature and
 * Spring derives the SQL from the name, e.g.
 *   List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email);
 */
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
}
