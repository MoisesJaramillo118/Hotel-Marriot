package pe.utp.marriott.habitaciones.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.habitaciones.modelo.TipoHabitacion;
import pe.utp.marriott.habitaciones.servicio.HabitacionService;

@RestController
@RequestMapping("/api/v1/habitaciones")
public class HabitacionController {

    private final HabitacionService service;

    public HabitacionController(HabitacionService service) {
        this.service = service;
    }

    /** Con 'desde' y 'hasta' devuelve solo las libres en ese rango; sin fechas, el inventario. */
    @GetMapping
    public DisponibilidadResponse consultar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) TipoHabitacion tipo) {
        List<HabitacionResponse> lista = service.consultar(desde, hasta, tipo).stream()
                .map(HabitacionResponse::de)
                .toList();
        return new DisponibilidadResponse(lista, lista.size());
    }

    @GetMapping("/{id}")
    public HabitacionResponse obtener(@PathVariable String id) {
        return HabitacionResponse.de(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody HabitacionRequest req) {
        HabitacionResponse creada = HabitacionResponse.de(service.crear(req));
        return ResponseEntity.created(URI.create("/api/v1/habitaciones/" + creada.id())).body(creada);
    }

    @PostMapping("/{id}/bloqueo")
    public BloqueoResponse bloquear(@PathVariable String id, @Valid @RequestBody BloqueoRequest req) {
        return BloqueoResponse.de(service.bloquear(id, req));
    }

    @DeleteMapping("/bloqueos/{reservaId}")
    public Map<String, Object> liberar(@PathVariable String reservaId) {
        return Map.of("reserva_id", reservaId, "liberados", service.liberar(reservaId));
    }
}
