package co.com.email.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinatarioEmail {

    private String rolActor;
    private String rutActor;
    private String email;
    private String tipoActor;
}
