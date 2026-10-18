package pe.utp.marriott.reservas.integracion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import pe.utp.marriott.reservas.error.ApiException;

/** Base de los clientes REST hacia los demás servicios: tiempos de espera y traducción de errores. */
public abstract class ClienteRemotoBase {

    protected final RestClient http;
    private final String servicio;
    private final ObjectMapper mapper;

    protected ClienteRemotoBase(String servicio, String urlBase, RestClient.Builder builder, ObjectMapper mapper,
                                Duration timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        this.http = builder.clone().baseUrl(urlBase).requestFactory(factory).build();
        this.servicio = servicio;
        this.mapper = mapper;
    }

    protected <T> T llamar(Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (HttpClientErrorException e) {
            throw new ErrorRemoto(servicio, e.getStatusCode().value(), leer(e.getResponseBodyAsString()));
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "SERVICIO_NO_DISPONIBLE",
                    "El servicio " + servicio + " no responde. Intente nuevamente en unos minutos.");
        }
    }

    private Map<String, Object> leer(String cuerpo) {
        try {
            return mapper.readValue(cuerpo, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of("mensaje", cuerpo == null ? "" : cuerpo);
        }
    }
}
