package co.vinod.controller;

import co.vinod.entity.Contact;
import co.vinod.entity.ContactList;
import co.vinod.entity.ErrorInfo;
import co.vinod.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Contact management API with support for JSON, XML, CSV, and text formats")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Get all contacts with various format supports and pagination
    @Operation(
            summary = "Get all contacts",
            description = "Retrieves a paginated list of contacts with support for different formats (JSON, XML, CSV, text)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contacts",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)),
                            @Content(mediaType = "application/xml"),
                            @Content(mediaType = "text/csv"),
                            @Content(mediaType = "text/plain")
                    })
    })
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    "text/csv",
                    MediaType.TEXT_PLAIN_VALUE
            }
    )
    public ResponseEntity<?> getAllContacts(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @Parameter(description = "Response format (json, xml, csv, txt)") 
            @RequestParam(value = "format", required = false) String format,
            @Parameter(description = "Page number (1-based)") 
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") 
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        // Adjust page for zero-based paging in Spring
        int adjustedPage = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(adjustedPage, limit);
        Page<Contact> contactPage = contactService.getAllContactsPaginated(pageable);
        
        // Determine content type
        String contentType = determineContentType(acceptHeader, format);
        
        switch (contentType) {
            case MediaType.APPLICATION_XML_VALUE:
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
                        .body(new ContactList(contactPage.getContent()));
            case "text/csv":
                return ResponseEntity.ok().contentType(MediaType.valueOf("text/csv"))
                        .body(convertToCsv(contactPage.getContent()));
            case MediaType.TEXT_PLAIN_VALUE:
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                        .body(convertToText(contactPage.getContent()));
            default: // JSON
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(contactPage);
        }
    }

    // Get one contact by ID
    @Operation(
            summary = "Get a contact by ID",
            description = "Retrieves a specific contact by its ID with support for different formats"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the contact",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)),
                            @Content(mediaType = "application/xml"),
                            @Content(mediaType = "text/csv"),
                            @Content(mediaType = "text/plain")
                    }),
            @ApiResponse(responseCode = "404", description = "Contact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    "text/csv",
                    MediaType.TEXT_PLAIN_VALUE
            }
    )
    public ResponseEntity<?> getContactById(
            @Parameter(description = "ID of the contact to retrieve") 
            @PathVariable String id,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @Parameter(description = "Response format (json, xml, csv, txt)") 
            @RequestParam(value = "format", required = false) String format
    ) {
        Optional<Contact> contact = contactService.getContactById(id);
        
        if (contact.isPresent()) {
            // Determine content type
            String contentType = determineContentType(acceptHeader, format);
            
            switch (contentType) {
                case MediaType.APPLICATION_XML_VALUE:
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
                            .body(contact.get());
                case "text/csv":
                    List<Contact> singleContact = List.of(contact.get());
                    return ResponseEntity.ok().contentType(MediaType.valueOf("text/csv"))
                            .body(convertToCsv(singleContact));
                case MediaType.TEXT_PLAIN_VALUE:
                    return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                            .body(convertToText(List.of(contact.get())));
                default: // JSON
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                            .body(contact.get());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorInfo("Contact with ID " + id + " not found"));
        }
    }

    // Create a new contact
    @Operation(
            summary = "Create a new contact",
            description = "Creates a new contact record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact successfully created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)),
                            @Content(mediaType = "application/xml")
                    })
    })
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> createContact(
            @Parameter(description = "Contact information", required = true) 
            @RequestBody Contact contact,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @Parameter(description = "Response format (json, xml)") 
            @RequestParam(value = "format", required = false) String format
    ) {
        Contact savedContact = contactService.createContact(contact);
        
        String contentType = determineContentType(acceptHeader, format);
        
        if (MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(savedContact);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(savedContact);
        }
    }

    // Update an existing contact
    @Operation(
            summary = "Update a contact",
            description = "Updates an existing contact by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact successfully updated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)),
                            @Content(mediaType = "application/xml")
                    }),
            @ApiResponse(responseCode = "404", description = "Contact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> updateContact(
            @Parameter(description = "ID of the contact to update", required = true) 
            @PathVariable String id,
            @Parameter(description = "Updated contact information", required = true) 
            @RequestBody Contact contact,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @Parameter(description = "Response format (json, xml)") 
            @RequestParam(value = "format", required = false) String format
    ) {
        Optional<Contact> updatedContact = contactService.updateContact(id, contact);
        
        if (updatedContact.isPresent()) {
            String contentType = determineContentType(acceptHeader, format);
            
            if (MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(updatedContact.get());
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedContact.get());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorInfo("Contact with ID " + id + " not found"));
        }
    }

    // Delete a contact
    @Operation(
            summary = "Delete a contact",
            description = "Deletes a contact by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Contact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorInfo.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(
            @Parameter(description = "ID of the contact to delete", required = true) 
            @PathVariable String id
    ) {
        boolean deleted = contactService.deleteContact(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorInfo("Contact with ID " + id + " not found"));
        }
    }

    // Helper methods
    private String determineContentType(String acceptHeader, String format) {
        // First check the format parameter
        if (format != null) {
            switch (format.toLowerCase()) {
                case "xml":
                    return MediaType.APPLICATION_XML_VALUE;
                case "csv":
                    return "text/csv";
                case "text":
                case "txt":
                    return MediaType.TEXT_PLAIN_VALUE;
                case "json":
                    return MediaType.APPLICATION_JSON_VALUE;
            }
        }
        
        // Then check the Accept header
        if (acceptHeader != null) {
            if (acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
                return MediaType.APPLICATION_XML_VALUE;
            } else if (acceptHeader.contains("text/csv")) {
                return "text/csv";
            } else if (acceptHeader.contains(MediaType.TEXT_PLAIN_VALUE)) {
                return MediaType.TEXT_PLAIN_VALUE;
            }
        }
        
        // Default to JSON
        return MediaType.APPLICATION_JSON_VALUE;
    }

    private String convertToCsv(List<Contact> contacts) {
        StringWriter writer = new StringWriter();
        
        String[] HEADERS = {
                "ID", "First Name", "Last Name", "Gender", "Email", "Phone", 
                "Address", "City", "State", "Country", "Pincode", "Picture"
        };
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            for (Contact contact : contacts) {
                csvPrinter.printRecord(
                        contact.getId(),
                        contact.getFirstname(),
                        contact.getLastname(),
                        contact.getGender(),
                        contact.getEmail(),
                        contact.getPhone(),
                        contact.getAddress(),
                        contact.getCity(),
                        contact.getState(),
                        contact.getCountry(),
                        contact.getPincode(),
                        contact.getPicture()
                );
            }
        } catch (IOException e) {
            return "Error generating CSV: " + e.getMessage();
        }
        
        return writer.toString();
    }

    private String convertToText(List<Contact> contacts) {
        StringBuilder builder = new StringBuilder();
        
        for (Contact contact : contacts) {
            builder.append("ID: ").append(contact.getId()).append("\n");
            builder.append("Name: ").append(contact.getFirstname()).append(" ").append(contact.getLastname()).append("\n");
            builder.append("Gender: ").append(contact.getGender()).append("\n");
            builder.append("Email: ").append(contact.getEmail()).append("\n");
            builder.append("Phone: ").append(contact.getPhone()).append("\n");
            builder.append("Address: ").append(contact.getAddress()).append("\n");
            builder.append("City: ").append(contact.getCity()).append("\n");
            builder.append("State: ").append(contact.getState()).append("\n");
            builder.append("Country: ").append(contact.getCountry()).append("\n");
            builder.append("Pincode: ").append(contact.getPincode()).append("\n");
            builder.append("Picture: ").append(contact.getPicture()).append("\n");
            builder.append("------------------------------------------\n");
        }
        
        return builder.toString();
    }
} 