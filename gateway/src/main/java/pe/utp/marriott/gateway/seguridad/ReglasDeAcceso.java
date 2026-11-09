package pe.utp.marriott.gateway.seguridad;

import java.util.List;
import java.util.Set;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Qué rol pide cada operación. Todo /api/v1 exige un JWT válido; estas reglas agregan
 * cuándo además hace falta ser personal del hotel (ADMIN). La primera regla que calza decide.
 */
public final class ReglasDeAcceso {

    public static final String ADMIN = "ADMIN";
    public static final String PARTNER = "PARTNER";

    private static final Set<HttpMethod> TODOS = Set.of();
    private static final Set<HttpMethod> ESCRITURAS =
            Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE);

    private record Regla(Set<HttpMethod> metodos, PathPattern ruta, String rol) {

        boolean calza(HttpMethod metodo, PathContainer path) {
            return (metodos.isEmpty() || metodos.contains(metodo)) && ruta.matches(path);
        }
    }

    private static final PathPatternParser PARSER = new PathPatternParser();

    private static final List<Regla> REGLAS = List.of(
            // Interoperabilidad entre hoteles: consultar el contacto compartible es solo para
            // sistemas asociados (rol PARTNER). Debe ir ANTES de la regla de /clientes (ADMIN).
            regla(Set.of(HttpMethod.GET), "/api/v1/clientes/compartible", PARTNER),
            // El cliente puede reservar un tour; el resto de escrituras de catálogo es del personal.
            regla(Set.of(HttpMethod.POST), "/api/v1/tours/{id}/reserva", null),
            regla(ESCRITURAS, "/api/v1/habitaciones/**", ADMIN),
            regla(ESCRITURAS, "/api/v1/paquetes/**", ADMIN),
            regla(ESCRITURAS, "/api/v1/tours/**", ADMIN),
            // Listar todos los clientes es del personal; registrarse y ver una ficha, de cualquiera.
            regla(Set.of(HttpMethod.GET), "/api/v1/clientes", ADMIN),
            // Pagos y Notificaciones son utilidades internas: las usa Reservas por la red interna.
            regla(TODOS, "/api/v1/pagos/**", ADMIN),
            regla(TODOS, "/api/v1/notificaciones/**", ADMIN));

    private static final List<PathPattern> PUBLICAS = List.of(
            PARSER.parse("/api/v1/auth/**"),
            PARSER.parse("/actuator/health"));

    private ReglasDeAcceso() {
    }

    public static boolean esPublica(String ruta) {
        PathContainer path = PathContainer.parsePath(ruta);
        return PUBLICAS.stream().anyMatch(p -> p.matches(path));
    }

    /** Devuelve el rol requerido, o null si basta con estar autenticado. */
    public static String rolRequerido(HttpMethod metodo, String ruta) {
        PathContainer path = PathContainer.parsePath(ruta);
        return REGLAS.stream()
                .filter(r -> r.calza(metodo, path))
                .findFirst()
                .map(Regla::rol)
                .orElse(null);
    }

    private static Regla regla(Set<HttpMethod> metodos, String ruta, String rol) {
        return new Regla(metodos, PARSER.parse(ruta), rol);
    }
}
