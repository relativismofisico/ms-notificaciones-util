package co.com.email.service;

import co.com.email.domain.entities.Person;
import co.com.email.domain.entities.User;
import co.com.email.repositories.PersonRepository;
import co.com.email.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEmailService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public String getEmailByUsername(String username) {
        return findPersonByUsername(username).getEmail();
    }

    public String getFullNameByUsername(String username) {
        Person person = findPersonByUsername(username);
        return person.getFirstName() + " " + person.getFirstLastname();
    }

    private Person findPersonByUsername(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return personRepository
                .findById(user.getPerson())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    }
}
