package pe.utp.marriott.pagos.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.marriott.pagos.servicio.PagoService;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    /** 200 si la pasarela aprueba; 402 si rechaza. */
    @PostMapping
    public PagoResponse cobrar(@Valid @RequestBody PagoRequest req) {
        return PagoResponse.de(service.cobrar(req));
    }

    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable String id) {
        return PagoResponse.de(service.obtener(id));
    }

    @PostMapping("/{txnId}/reembolso")
    public PagoResponse reembolsar(@PathVariable String txnId) {
        return PagoResponse.de(service.reembolsar(txnId));
    }
}
