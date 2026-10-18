package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ToursClient extends ClienteRemotoBase {

    public ToursClient(@Value("${servicios.tours}") String url, @Value("${servicios.timeout}") Duration timeout,
                       RestClient.Builder builder, ObjectMapper mapper) {
        super("Tours", url, builder, mapper, timeout);
    }

    public Remotos.Tour obtener(String id) {
        return llamar(() -> http.get().uri("/api/v1/tours/{id}", id).retrieve().body(Remotos.Tour.class));
    }

    public void reservar(String tourId, String clienteId, String reservaId) {
        llamar(() -> http.post().uri("/api/v1/tours/{id}/reserva", tourId)
                .body(Map.of("cliente_id", clienteId, "reserva_id", reservaId, "cupos", 1))
                .retrieve().toBodilessEntity());
    }

    public void cancelar(String reservaId) {
        llamar(() -> http.delete().uri("/api/v1/tours/reservas/{reservaId}", reservaId)
                .retrieve().toBodilessEntity());
    }
}
