package pe.utp.marriott.notificaciones.api;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.notificaciones.servicio.NotificacionService;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    /** 202 Accepted: queda encolada y se envía en segundo plano. */
    @PostMapping
    public ResponseEntity<NotificacionResponse> encolar(@Valid @RequestBody NotificacionRequest req) {
        NotificacionResponse n = NotificacionResponse.de(service.encolar(req));
        return ResponseEntity.accepted().location(URI.create("/api/v1/notificaciones/" + n.id())).body(n);
    }

    @GetMapping("/{id}")
    public NotificacionResponse obtener(@PathVariable String id) {
        return NotificacionResponse.de(service.obtener(id));
    }
}
