package co.vinod.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String picture;
}
