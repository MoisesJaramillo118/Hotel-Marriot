package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificacionesClient extends ClienteRemotoBase {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesClient.class);

    public NotificacionesClient(@Value("${servicios.notificaciones}") String url,
                                @Value("${servicios.timeout}") Duration timeout,
                                RestClient.Builder builder, ObjectMapper mapper) {
        super("Notificaciones", url, builder, mapper, timeout);
    }

    /**
     * Avisar es "mejor esfuerzo": si Notificaciones está caído, la reserva igual queda confirmada
     * (el cobro ya se hizo) y solo se registra el problema.
     */
    public void avisar(String destinatario, String plantilla, Map<String, String> datos) {
        try {
            llamar(() -> http.post().uri("/api/v1/notificaciones")
                    .body(Map.of("canal", "EMAIL", "destinatario", destinatario, "plantilla", plantilla, "datos", datos))
                    .retrieve().toBodilessEntity());
        } catch (RuntimeException e) {
            log.warn("No se pudo notificar {} a {}: {}", plantilla, destinatario, e.getMessage());
        }
    }
}
