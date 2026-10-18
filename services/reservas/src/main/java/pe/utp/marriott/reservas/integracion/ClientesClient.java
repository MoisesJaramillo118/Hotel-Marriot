package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ClientesClient extends ClienteRemotoBase {

    public ClientesClient(@Value("${servicios.clientes}") String url, @Value("${servicios.timeout}") Duration timeout,
                          RestClient.Builder builder, ObjectMapper mapper) {
        super("Clientes", url, builder, mapper, timeout);
    }

    public Remotos.Cliente obtener(String id) {
        return llamar(() -> http.get().uri("/api/v1/clientes/{id}", id).retrieve().body(Remotos.Cliente.class));
    }
}
