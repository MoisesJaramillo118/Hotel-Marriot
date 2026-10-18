package pe.utp.marriott.reservas.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.utp.marriott.reservas.api.CrearReservaRequest;
import pe.utp.marriott.reservas.api.ReservaResponse;
import pe.utp.marriott.reservas.comun.Fechas;
import pe.utp.marriott.reservas.error.ApiException;
import pe.utp.marriott.reservas.modelo.SolicitudIdempotente;
import pe.utp.marriott.reservas.modelo.SolicitudIdempotenteRepository;

/**
 * Protege POST /reservas con Idempotency-Key: la misma clave con el mismo cuerpo devuelve la
 * misma respuesta (201, 402 o 409) sin volver a bloquear ni a cobrar.
 */
@Service
public class ReservasIdempotentes {

    public static final String CABECERA_REPETIDA = "Idempotent-Replayed";

    private final SolicitudIdempotenteRepository solicitudes;
    private final OrquestadorReservas orquestador;
    private final ObjectMapper mapper;

    public ReservasIdempotentes(SolicitudIdempotenteRepository solicitudes, OrquestadorReservas orquestador,
                                ObjectMapper mapper) {
        this.solicitudes = solicitudes;
        this.orquestador = orquestador;
        this.mapper = mapper;
    }

    public ResponseEntity<?> crear(String clave, CrearReservaRequest req) {
        if (clave == null || clave.isBlank() || clave.length() > 100) {
            throw ApiException.solicitudInvalida("IDEMPOTENCY_KEY_REQUERIDA",
                    "Envíe la cabecera Idempotency-Key (hasta 100 caracteres) para crear una reserva.");
        }
        String hash = hash(req);
        if (solicitudes.tomar(clave, hash, Fechas.ahora()) == 0) {
            return repetir(clave, hash);
        }

        try {
            ReservaResponse creada = ReservaResponse.de(orquestador.crear(req));
            completar(clave, HttpStatus.CREATED.value(), creada);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (ApiException e) {
            if (e.getStatus().is4xxClientError()) {
                // Un rechazo de negocio es una respuesta definitiva: se repite igual.
                completar(clave, e.getStatus().value(), cuerpoError(e));
            } else {
                // Una caída de otro servicio no lo es: se libera la clave para reintentar.
                solicitudes.deleteById(clave);
            }
            throw e;
        } catch (RuntimeException e) {
            solicitudes.deleteById(clave);
            throw e;
        }
    }

    private ResponseEntity<?> repetir(String clave, String hash) {
        SolicitudIdempotente previa = solicitudes.findById(clave).orElseThrow(() -> new ApiException(
                HttpStatus.CONFLICT, "SOLICITUD_EN_PROCESO", "Reintente en unos segundos."));
        if (!previa.getHashSolicitud().equals(hash)) {
            throw ApiException.conflicto("IDEMPOTENCY_KEY_REUTILIZADA",
                    "Esa Idempotency-Key ya se usó con otra solicitud. Genere una clave nueva.");
        }
        if (SolicitudIdempotente.EN_PROCESO.equals(previa.getEstado())) {
            throw ApiException.conflicto("SOLICITUD_EN_PROCESO",
                    "Esta reserva aún se está procesando. Reintente en unos segundos.");
        }
        try {
            JsonNode cuerpo = mapper.readTree(previa.getCuerpo());
            return ResponseEntity.status(previa.getHttpStatus()).header(CABECERA_REPETIDA, "true").body(cuerpo);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Respuesta guardada ilegible para la clave " + clave, e);
        }
    }

    private void completar(String clave, int status, Object cuerpo) {
        SolicitudIdempotente s = solicitudes.findById(clave).orElseThrow();
        s.setEstado(SolicitudIdempotente.COMPLETADA);
        s.setHttpStatus(status);
        try {
            s.setCuerpo(mapper.writeValueAsString(cuerpo));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        solicitudes.save(s);
    }

    private static Map<String, Object> cuerpoError(ApiException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.getCodigo());
        body.put("mensaje", e.getMessage());
        body.putAll(e.getExtra());
        return body;
    }

    private String hash(CrearReservaRequest req) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(mapper.writeValueAsString(req).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
