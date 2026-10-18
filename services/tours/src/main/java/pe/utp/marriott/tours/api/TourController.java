package pe.utp.marriott.tours.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.tours.servicio.TourService;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    private final TourService service;

    public TourController(TourService service) {
        this.service = service;
    }

    @GetMapping
    public List<TourResponse> buscar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.buscar(fecha).stream().map(TourResponse::de).toList();
    }

    @GetMapping("/{id}")
    public TourResponse obtener(@PathVariable String id) {
        return TourResponse.de(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TourResponse> crear(@Valid @RequestBody TourRequest req) {
        TourResponse creado = TourResponse.de(service.crear(req));
        return ResponseEntity.created(URI.create("/api/v1/tours/" + creado.id())).body(creado);
    }

    @PostMapping("/{id}/reserva")
    public ResponseEntity<ReservaTourResponse> reservar(@PathVariable String id,
                                                        @Valid @RequestBody ReservaTourRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservaTourResponse.de(service.reservar(id, req)));
    }

    @DeleteMapping("/reservas/{reservaId}")
    public Map<String, Object> cancelarPorReserva(@PathVariable String reservaId) {
        return Map.of("reserva_id", reservaId, "cancelados", service.cancelarPorReserva(reservaId));
    }
}
