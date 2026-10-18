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
}
