package co.vinod.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class ContactList {
    @XmlElement(name = "contact")
    private ArrayList<Contact> contacts = new ArrayList<>();

    public ContactList(List<Contact> contacts) {
        this.contacts.addAll(contacts);
    }
}