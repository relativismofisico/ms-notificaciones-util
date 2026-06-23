package co.com.email.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
//@Builder
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ide_user")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    private String password;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "company_ide")
    private Long company;

    @Column(name = "person_IDE")
    private Long person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_user_id", referencedColumnName = "ide", nullable = false)
    private StatesUsers stateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_user_IDE", referencedColumnName = "IDE_ROL")
    private RolUser rolUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }
}