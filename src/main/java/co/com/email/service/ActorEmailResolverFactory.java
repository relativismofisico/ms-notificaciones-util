package co.com.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActorEmailResolverFactory {

    private final List<ActorEmailResolver> resolvers;

    public ActorEmailResolver get(String tipoActor) {
        return resolvers.stream()
                .filter(r -> r.soporta(tipoActor))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No existe resolver para tipoActor: " + tipoActor));
    }
}