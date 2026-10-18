package pe.utp.marriott.habitaciones.api;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import pe.utp.marriott.habitaciones.modelo.Bloqueo;
import pe.utp.marriott.habitaciones.modelo.EstadoBloqueo;

public record BloqueoResponse(String id, String habitacionId, String reservaId, LocalDate desde, LocalDate hasta,
                              EstadoBloqueo estado, OffsetDateTime creadoEn) {

    public static BloqueoResponse de(Bloqueo b) {
        return new BloqueoResponse(b.getId(), b.getHabitacionId(), b.getReservaId(), b.getDesde(), b.getHasta(),
                b.getEstado(), b.getCreadoEn());
    }
}
