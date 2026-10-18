package pe.utp.marriott.reservas.error;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.utp.marriott.reservas.integracion.ErrorRemoto;

/** Un 4xx inesperado de otro servicio (fuera de los casos que el orquestador traduce) es un 502. */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorRemotoHandler {

    @ExceptionHandler(ErrorRemoto.class)
    public ResponseEntity<Map<String, Object>> remoto(ErrorRemoto e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.codigo());
        body.put("mensaje", "El servicio " + e.getServicio() + " rechazó la operación: " + e.mensaje());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
