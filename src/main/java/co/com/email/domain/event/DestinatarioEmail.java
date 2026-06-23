package co.com.email.domain.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinatarioEmail {

    private String rolActor; // PAGADOR, PROVEEDOR, FONDEADOR
    private String rutActor;
    private String email;
    private String tipoActor;

}
