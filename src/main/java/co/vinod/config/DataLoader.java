package co.vinod.config;

import co.vinod.entity.Contact;
import co.vinod.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContactRepository contactRepository;

    @Autowired
    public DataLoader(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void run(String... args) {
        // Load sample data only if the repository is empty
        if (contactRepository.count() == 0) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        // Sample Contact 1
        Contact contact1 = new Contact();
        contact1.setId(UUID.randomUUID().toString());
        contact1.setFirstname("John");
        contact1.setLastname("Doe");
        contact1.setGender("Male");
        contact1.setEmail("john.doe@example.com");
        contact1.setPhone("555-123-4567");
        contact1.setAddress("123 Main St");
        contact1.setCity("New York");
        contact1.setState("NY");
        contact1.setCountry("USA");
        contact1.setPincode("10001");
        contact1.setPicture("https://randomuser.me/api/portraits/men/1.jpg");
        contactRepository.save(contact1);

        // Sample Contact 2
        Contact contact2 = new Contact();
        contact2.setId(UUID.randomUUID().toString());
        contact2.setFirstname("Jane");
        contact2.setLastname("Smith");
        contact2.setGender("Female");
        contact2.setEmail("jane.smith@example.com");
        contact2.setPhone("555-987-6543");
        contact2.setAddress("456 Oak Ave");
        contact2.setCity("San Francisco");
        contact2.setState("CA");
        contact2.setCountry("USA");
        contact2.setPincode("94107");
        contact2.setPicture("https://randomuser.me/api/portraits/women/2.jpg");
        contactRepository.save(contact2);

        // Sample Contact 3
        Contact contact3 = new Contact();
        contact3.setId(UUID.randomUUID().toString());
        contact3.setFirstname("Robert");
        contact3.setLastname("Johnson");
        contact3.setGender("Male");
        contact3.setEmail("robert.johnson@example.com");
        contact3.setPhone("555-567-8901");
        contact3.setAddress("789 Pine St");
        contact3.setCity("Chicago");
        contact3.setState("IL");
        contact3.setCountry("USA");
        contact3.setPincode("60601");
        contact3.setPicture("https://randomuser.me/api/portraits/men/3.jpg");
        contactRepository.save(contact3);
    }
} 