package co.com.email.service

import co.com.email.domain.entities.Person
import co.com.email.domain.entities.User
import co.com.email.repositories.PersonRepository
import co.com.email.repositories.UserRepository
import spock.lang.Specification

class UserEmailServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    PersonRepository personRepository = Mock()
    UserEmailService service = new UserEmailService(userRepository, personRepository)

    def buildUser(Long personId) {
        User user = new User()
        user.person = personId
        return user
    }

    def buildPerson(String firstName, String lastName, String email) {
        Person person = new Person()
        person.firstName = firstName
        person.firstLastname = lastName
        person.email = email
        return person
    }

    def "getEmailByUsername retorna email cuando usuario y persona existen"() {
        given:
        def user = buildUser(42L)
        def person = buildPerson("Juan", "Pérez", "juan@test.com")
        userRepository.findByUsername("jperez") >> Optional.of(user)
        personRepository.findById(42L) >> Optional.of(person)

        when:
        def result = service.getEmailByUsername("jperez")

        then:
        result == "juan@test.com"
    }

    def "getEmailByUsername lanza RuntimeException cuando usuario no existe"() {
        given:
        userRepository.findByUsername("no-existe") >> Optional.empty()

        when:
        service.getEmailByUsername("no-existe")

        then:
        def ex = thrown(RuntimeException)
        ex.message == "Usuario no encontrado"
    }

    def "getEmailByUsername lanza RuntimeException cuando persona no existe"() {
        given:
        def user = buildUser(99L)
        userRepository.findByUsername("jperez") >> Optional.of(user)
        personRepository.findById(99L) >> Optional.empty()

        when:
        service.getEmailByUsername("jperez")

        then:
        def ex = thrown(RuntimeException)
        ex.message == "Persona no encontrada"
    }

    def "getFullNameByUsername retorna nombre completo"() {
        given:
        def user = buildUser(1L)
        def person = buildPerson("Ana", "García", "ana@test.com")
        userRepository.findByUsername("agarcia") >> Optional.of(user)
        personRepository.findById(1L) >> Optional.of(person)

        when:
        def result = service.getFullNameByUsername("agarcia")

        then:
        result == "Ana García"
    }

    def "getFullNameByUsername lanza RuntimeException cuando usuario no existe"() {
        given:
        userRepository.findByUsername("no-existe") >> Optional.empty()

        when:
        service.getFullNameByUsername("no-existe")

        then:
        thrown(RuntimeException)
    }
}