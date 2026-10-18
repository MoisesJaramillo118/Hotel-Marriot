package pe.utp.marriott.pagos.servicio;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.pagos.api.PagoRequest;
import pe.utp.marriott.pagos.comun.Fechas;
import pe.utp.marriott.pagos.comun.Secuencias;
import pe.utp.marriott.pagos.error.ApiException;
import pe.utp.marriott.pagos.modelo.EstadoPago;
import pe.utp.marriott.pagos.modelo.Pago;
import pe.utp.marriott.pagos.modelo.PagoRepository;
import pe.utp.marriott.pagos.pasarela.PasarelaPagos;

@Service
public class PagoService {

    private final PagoRepository repo;
    private final PasarelaPagos pasarela;
    private final Secuencias secuencias;

    public PagoService(PagoRepository repo, PasarelaPagos pasarela, Secuencias secuencias) {
        this.repo = repo;
        this.pasarela = pasarela;
        this.secuencias = secuencias;
    }

    /** Cobra y registra el intento. Un rechazo también queda guardado, y se informa con 402. */
    @Transactional(noRollbackFor = ApiException.class)
    public Pago cobrar(PagoRequest req) {
        String moneda = req.moneda() == null ? "PEN" : req.moneda();
        PasarelaPagos.ResultadoCobro resultado = pasarela.cobrar(req.monto(), moneda, req.tokenTarjeta());

        Pago p = new Pago();
        p.setId(secuencias.siguiente("seq_pago", "PAG", 6));
        p.setMonto(req.monto());
        p.setMoneda(moneda);
        p.setMetodo(req.metodo());
        String token = req.tokenTarjeta();
        p.setTokenFinal(token.substring(Math.max(0, token.length() - 4)));
        p.setReferencia(req.referencia());
        p.setCreadoEn(Fechas.ahora());
        if (resultado.aprobado()) {
            p.setEstado(EstadoPago.APROBADO);
            p.setTxnId(resultado.txnId());
            return repo.save(p);
        }
        p.setEstado(EstadoPago.RECHAZADO);
        p.setMotivo(resultado.motivo());
        repo.save(p);
        throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "PAGO_RECHAZADO", resultado.motivo(),
                Map.of("pago_id", p.getId()));
    }

    @Transactional(readOnly = true)
    public Pago obtener(String id) {
        return repo.findById(id).orElseThrow(() -> ApiException.noEncontrado("El pago", id));
    }

    @Transactional
    public Pago reembolsar(String txnId) {
        Pago p = repo.findByTxnId(txnId).orElseThrow(() -> ApiException.noEncontrado("La transacción", txnId));
        if (p.getEstado() == EstadoPago.REEMBOLSADO) {
            return p;
        }
        if (p.getEstado() != EstadoPago.APROBADO) {
            throw ApiException.conflicto("PAGO_NO_REEMBOLSABLE", "Solo se reembolsa un pago aprobado.");
        }
        if (!pasarela.reembolsar(txnId, p.getMonto())) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "PASARELA_NO_DISPONIBLE",
                    "La pasarela no confirmó el reembolso. Intente nuevamente.");
        }
        p.setEstado(EstadoPago.REEMBOLSADO);
        return p;
    }
}
