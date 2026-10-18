package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaquetesClient extends ClienteRemotoBase {

    public PaquetesClient(@Value("${servicios.paquetes}") String url, @Value("${servicios.timeout}") Duration timeout,
                          RestClient.Builder builder, ObjectMapper mapper) {
        super("Paquetes", url, builder, mapper, timeout);
    }

    public Remotos.Paquete obtener(String id) {
        return llamar(() -> http.get().uri("/api/v1/paquetes/{id}", id).retrieve().body(Remotos.Paquete.class));
    }
}
