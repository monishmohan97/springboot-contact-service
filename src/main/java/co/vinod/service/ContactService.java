package co.vinod.service;

import co.vinod.entity.Contact;
import co.vinod.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository repository;

    @Autowired
    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    public List<Contact> getAllContacts() {
        return repository.findAll();
    }

    public Page<Contact> getAllContactsPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Contact> getContactById(String id) {
        return repository.findById(id);
    }

    public Contact createContact(Contact contact) {
        if (contact.getId() == null || contact.getId().isEmpty()) {
            contact.setId(UUID.randomUUID().toString());
        }
        return repository.save(contact);
    }

    public Optional<Contact> updateContact(String id, Contact contact) {
        if (repository.existsById(id)) {
            contact.setId(id);
            return Optional.of(repository.save(contact));
        }
        return Optional.empty();
    }

    public boolean deleteContact(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
} 