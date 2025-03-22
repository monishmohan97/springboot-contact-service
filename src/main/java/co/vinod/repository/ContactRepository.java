package co.vinod.repository;

import co.vinod.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    // Basic CRUD operations are provided by JpaRepository
} 