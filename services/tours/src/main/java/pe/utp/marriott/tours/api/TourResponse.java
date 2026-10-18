package pe.utp.marriott.tours.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import pe.utp.marriott.tours.modelo.Tour;

public record TourResponse(String id, String nombre, LocalDate fecha, int cupoTotal, int cupoDisponible,
                           BigDecimal precio, String moneda) {

    public static TourResponse de(Tour t) {
        return new TourResponse(t.getId(), t.getNombre(), t.getFecha(), t.getCupoTotal(), t.getCupoDisponible(),
                t.getPrecio(), t.getMoneda());
    }
}
