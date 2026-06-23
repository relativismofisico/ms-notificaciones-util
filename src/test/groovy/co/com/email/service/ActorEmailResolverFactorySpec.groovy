package co.com.email.service

import spock.lang.Specification

class ActorEmailResolverFactorySpec extends Specification {

    def "get retorna resolver que soporta el tipoActor dado"() {
        given:
        ActorEmailResolver empresaResolver = Mock()
        empresaResolver.soporta("EMPRESA") >> true
        ActorEmailResolver usuarioResolver = Mock()
        usuarioResolver.soporta("EMPRESA") >> false

        ActorEmailResolverFactory factory = new ActorEmailResolverFactory([usuarioResolver, empresaResolver])

        when:
        def result = factory.get("EMPRESA")

        then:
        result == empresaResolver
    }

    def "get retorna resolver para usuario cuando tipoActor es USUARIO"() {
        given:
        ActorEmailResolver usuarioResolver = Mock()
        usuarioResolver.soporta("USUARIO") >> true
        ActorEmailResolver empresaResolver = Mock()
        empresaResolver.soporta("USUARIO") >> false

        ActorEmailResolverFactory factory = new ActorEmailResolverFactory([empresaResolver, usuarioResolver])

        when:
        def result = factory.get("USUARIO")

        then:
        result == usuarioResolver
    }

    def "get lanza RuntimeException cuando no existe resolver para el tipoActor"() {
        given:
        ActorEmailResolver resolver = Mock()
        resolver.soporta("DESCONOCIDO") >> false

        ActorEmailResolverFactory factory = new ActorEmailResolverFactory([resolver])

        when:
        factory.get("DESCONOCIDO")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("DESCONOCIDO")
    }
}