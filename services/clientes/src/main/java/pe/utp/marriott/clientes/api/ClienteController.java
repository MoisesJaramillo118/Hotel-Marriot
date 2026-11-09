package pe.utp.marriott.clientes.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.clientes.servicio.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody ClienteRequest req) {
        ClienteResponse creado = ClienteResponse.de(service.registrar(req));
        return ResponseEntity.created(URI.create("/api/v1/clientes/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public ClienteResponse obtener(@PathVariable String id) {
        return ClienteResponse.de(service.obtener(id));
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String email) {
        return service.listar(email).stream().map(ClienteResponse::de).toList();
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable String id, @Valid @RequestBody ClienteRequest req) {
        return ClienteResponse.de(service.actualizar(id, req));
    }

    /** Cuerpo para que el titular active o desactive el consentimiento de compartir datos. */
    public record ConsentimientoRequest(boolean comparteDatos) {
    }

    /**
     * El titular (o el personal, en su nombre) activa o desactiva el consentimiento para
     * compartir su contacto con hoteles asociados. Ruta literal declarada antes del patrón
     * de interoperabilidad para que Spring no la confunda con /{id}.
     */
    @PutMapping("/{id}/consentimiento")
    public ClienteResponse consentimiento(@PathVariable String id, @RequestBody ConsentimientoRequest req) {
        return ClienteResponse.de(service.actualizarConsentimiento(id, req.comparteDatos()));
    }

    /**
     * Interoperabilidad entre hoteles: un sistema asociado (rol PARTNER, autenticado por el
     * gateway) consulta por email el contacto básico de un cliente que haya dado su
     * consentimiento. Devuelve SOLO nombre, apellidos, email y teléfono.
     */
    @GetMapping("/compartible")
    public ClienteCompartibleResponse compartible(@RequestParam String email) {
        return ClienteCompartibleResponse.de(service.obtenerCompartiblePorEmail(email));
    }
}
