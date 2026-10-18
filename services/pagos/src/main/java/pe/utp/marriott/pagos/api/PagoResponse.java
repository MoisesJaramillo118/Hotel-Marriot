package pe.utp.marriott.pagos.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import pe.utp.marriott.pagos.modelo.EstadoPago;
import pe.utp.marriott.pagos.modelo.MetodoPago;
import pe.utp.marriott.pagos.modelo.Pago;

public record PagoResponse(String pagoId, EstadoPago estado, String txnId, BigDecimal monto, String moneda,
                           MetodoPago metodo, String referencia, OffsetDateTime creadoEn) {

    public static PagoResponse de(Pago p) {
        return new PagoResponse(p.getId(), p.getEstado(), p.getTxnId(), p.getMonto(), p.getMoneda(), p.getMetodo(),
                p.getReferencia(), p.getCreadoEn());
    }
}
