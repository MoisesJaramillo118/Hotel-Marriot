package pe.utp.marriott.tours.api;

import java.time.OffsetDateTime;
import pe.utp.marriott.tours.modelo.EstadoReservaTour;
import pe.utp.marriott.tours.modelo.ReservaTour;

public record ReservaTourResponse(String id, String tourId, String clienteId, String reservaId, int cupos,
                                  EstadoReservaTour estado, OffsetDateTime creadoEn) {

    public static ReservaTourResponse de(ReservaTour r) {
        return new ReservaTourResponse(r.getId(), r.getTourId(), r.getClienteId(), r.getReservaId(), r.getCupos(),
                r.getEstado(), r.getCreadoEn());
    }
}
