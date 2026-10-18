package pe.utp.marriott.reservas.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import pe.utp.marriott.reservas.modelo.EstadoReserva;
import pe.utp.marriott.reservas.modelo.Reserva;

public record ReservaResponse(
        String reservaId,
        EstadoReserva estado,
        String clienteId,
        String habitacionId,
        String tourId,
        String paqueteId,
        LocalDate desde,
        LocalDate hasta,
        int noches,
        BigDecimal total,
        String moneda,
        List<String> items,
        Pago pago,
        String motivo,
        OffsetDateTime creadoEn) {

    public record Pago(String estado, String txnId) {
    }

    public static ReservaResponse de(Reserva r) {
        Pago pago = r.getPagoEstado() == null ? null : new Pago(r.getPagoEstado(), r.getPagoTxnId());
        return new ReservaResponse(r.getId(), r.getEstado(), r.getClienteId(), r.getHabitacionId(), r.getTourId(),
                r.getPaqueteId(), r.getDesde(), r.getHasta(), r.getNoches(), r.getTotal(), r.getMoneda(),
                r.getListaItems(), pago, r.getMotivo(), r.getCreadoEn());
    }
}
