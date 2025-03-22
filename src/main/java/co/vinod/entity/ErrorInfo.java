package co.vinod.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class ErrorInfo {
    private String message;
    private Date timestamp = new Date();

    public ErrorInfo(String message){
        this.message = message;
    }
}
