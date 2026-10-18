package pe.utp.marriott.paquetes.api;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.paquetes.servicio.PaqueteService;

@RestController
@RequestMapping("/api/v1/paquetes")
public class PaqueteController {

    private final PaqueteService service;

    public PaqueteController(PaqueteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PaqueteResponse> buscar(@RequestParam(required = false) Boolean activo,
                                        @RequestParam(name = "precio_max", required = false) BigDecimal precioMax,
                                        @RequestParam(name = "q", required = false) String texto) {
        return service.buscar(activo, precioMax, texto).stream().map(PaqueteResponse::de).toList();
    }

    @GetMapping("/{id}")
    public PaqueteResponse obtener(@PathVariable String id) {
        return PaqueteResponse.de(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PaqueteResponse> crear(@Valid @RequestBody PaqueteRequest req) {
        PaqueteResponse creado = PaqueteResponse.de(service.crear(req));
        return ResponseEntity.created(URI.create("/api/v1/paquetes/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public PaqueteResponse actualizar(@PathVariable String id, @Valid @RequestBody PaqueteRequest req) {
        return PaqueteResponse.de(service.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public PaqueteResponse desactivar(@PathVariable String id) {
        return PaqueteResponse.de(service.desactivar(id));
    }
}
