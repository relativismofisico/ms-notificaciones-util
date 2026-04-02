package co.com.email.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ide_company")
    private Long ideCompany;

    @Column(name = "name_company")
    private String nameCompany;

    @Column(name = "nit", unique = true)
    private String nit;

    @Column(name = "email_company")
    private String emailCompany;
}
