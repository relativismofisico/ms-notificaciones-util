package co.com.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CanalProcessorFactory {

    private final List<CanalProcessor> processors;

    public CanalProcessor get(String canal) {

        return processors.stream()
                .filter(p -> p.soporta(canal))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No existe processor para canal " + canal)
                );
    }
}
