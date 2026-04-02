package co.com.email.service;

import co.com.email.domain.event.NotificacionEmailEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NegociacionCreadaTemplate implements EmailTemplate {

    @Override
    public String render(NotificacionEmailEvent event) {
        Map<String, Object> vars = event.getVariables();

        return "<html>" +
                "<body>" +
                "<h2>Nueva negociación creada</h2>" +
                "<p><b>ID:</b> " + vars.get("idNegociacion") + "</p>" +
                "<p><b>Tipo:</b> " + vars.get("tipo") + "</p>" +
                "<p><b>Total:</b> $" + vars.get("total") + "</p>" +
                "<p><b>Facturas:</b> " + vars.get("facturas") + "</p>" +
                "</body>" +
                "</html>";
    }
}
