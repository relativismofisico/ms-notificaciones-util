package co.com.email.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "person")
@Data
public class Person {

    @Id
    @Column(name = "IDE")
    private Long ide;

    @Column(name = "firts_name")
    private String firstName;

    @Column(name = "firts_lastname")
    private String firstLastname;

    private String email;
}
