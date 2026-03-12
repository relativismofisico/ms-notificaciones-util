package co.com.email.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "statesusers")
public class StatesUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ide")
    private Long id;
    private String description;

    @Column(name = "user_state_name")
    private String userStateName;

    public StatesUsers() {
    }
}
