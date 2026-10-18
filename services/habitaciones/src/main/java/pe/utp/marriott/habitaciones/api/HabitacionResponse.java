package pe.utp.marriott.habitaciones.api;

import java.math.BigDecimal;
import pe.utp.marriott.habitaciones.modelo.EstadoHabitacion;
import pe.utp.marriott.habitaciones.modelo.Habitacion;
import pe.utp.marriott.habitaciones.modelo.TipoHabitacion;

public record HabitacionResponse(String id, TipoHabitacion tipo, int piso, BigDecimal tarifaNoche, String moneda,
                                 EstadoHabitacion estado) {

    public static HabitacionResponse de(Habitacion h) {
        return new HabitacionResponse(h.getId(), h.getTipo(), h.getPiso(), h.getTarifaNoche(), h.getMoneda(),
                h.getEstado());
    }
}
