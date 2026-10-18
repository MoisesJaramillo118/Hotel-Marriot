package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PagosClient extends ClienteRemotoBase {

    public PagosClient(@Value("${servicios.pagos}") String url, @Value("${servicios.timeout}") Duration timeout,
                       RestClient.Builder builder, ObjectMapper mapper) {
        super("Pagos", url, builder, mapper, timeout);
    }

    public Remotos.Pago cobrar(BigDecimal monto, String moneda, String metodo, String token, String referencia) {
        Map<String, Object> cuerpo = Map.of("monto", monto, "moneda", moneda, "metodo", metodo,
                "token_tarjeta", token, "referencia", referencia);
        return llamar(() -> http.post().uri("/api/v1/pagos").body(cuerpo).retrieve().body(Remotos.Pago.class));
    }

    public Remotos.Pago reembolsar(String txnId) {
        return llamar(() -> http.post().uri("/api/v1/pagos/{txn}/reembolso", txnId).retrieve()
                .body(Remotos.Pago.class));
    }
}
