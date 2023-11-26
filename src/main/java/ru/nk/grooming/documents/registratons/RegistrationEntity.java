package ru.nk.grooming.documents.registratons;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private Long productId;
    private Long employeeId;
    private String comment;
    private int duration;
    private int price;
    private Long salonId;
    private Long userId;
}







