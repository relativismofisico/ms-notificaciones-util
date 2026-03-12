package co.com.email.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rol_user")
public class RolUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDE_ROL")
    private Long id;

    @Column(name = "rol_name")
    private String rolName;

    @Column(name = "rol_description")
    private String rolDescription;

    public RolUser() {
    }
}
