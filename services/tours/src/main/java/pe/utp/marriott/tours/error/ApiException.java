package pe.utp.marriott.tours.error;

import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Error de negocio con su código HTTP y un código legible ("SIN_DISPONIBILIDAD").
 * Todos los servicios responden el mismo formato: {"error", "mensaje", ...extra}.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String codigo;
    private final Map<String, Object> extra;

    public ApiException(HttpStatus status, String codigo, String mensaje) {
        this(status, codigo, mensaje, Map.of());
    }

    public ApiException(HttpStatus status, String codigo, String mensaje, Map<String, Object> extra) {
        super(mensaje);
        this.status = status;
        this.codigo = codigo;
        this.extra = extra;
    }

    public static ApiException noEncontrado(String recurso, String id) {
        return new ApiException(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", recurso + " " + id + " no existe.");
    }

    public static ApiException conflicto(String codigo, String mensaje) {
        return new ApiException(HttpStatus.CONFLICT, codigo, mensaje);
    }

    public static ApiException solicitudInvalida(String codigo, String mensaje) {
        return new ApiException(HttpStatus.BAD_REQUEST, codigo, mensaje);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCodigo() {
        return codigo;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }
}
