package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HabitacionesClient extends ClienteRemotoBase {

    public HabitacionesClient(@Value("${servicios.habitaciones}") String url,
                              @Value("${servicios.timeout}") Duration timeout,
                              RestClient.Builder builder, ObjectMapper mapper) {
        super("Habitaciones", url, builder, mapper, timeout);
    }

    public Remotos.Habitacion obtener(String id) {
        return llamar(() -> http.get().uri("/api/v1/habitaciones/{id}", id).retrieve()
                .body(Remotos.Habitacion.class));
    }

    public void bloquear(String habitacionId, String reservaId, LocalDate desde, LocalDate hasta) {
        llamar(() -> http.post().uri("/api/v1/habitaciones/{id}/bloqueo", habitacionId)
                .body(Map.of("reserva_id", reservaId, "desde", desde.toString(), "hasta", hasta.toString()))
                .retrieve().toBodilessEntity());
    }

    public void liberar(String reservaId) {
        llamar(() -> http.delete().uri("/api/v1/habitaciones/bloqueos/{reservaId}", reservaId)
                .retrieve().toBodilessEntity());
    }
}
