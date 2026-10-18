package pe.utp.marriott.reservas.integracion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Lo que el orquestador necesita de cada servicio (los campos que sobran se ignoran). */
public final class Remotos {

    private Remotos() {
    }

    public record Cliente(String id, String nombres, String apellidos, String email, String estado) {
    }

    public record Habitacion(String id, String tipo, BigDecimal tarifaNoche, String moneda, String estado) {
    }

    public record Disponibilidad(List<Habitacion> disponibles, int total) {
    }

    public record Tour(String id, String nombre, LocalDate fecha, int cupoDisponible, BigDecimal precio,
                       String moneda) {
    }

    public record Paquete(String id, String nombre, BigDecimal precio, String moneda, boolean activo) {
    }

    public record Pago(String pagoId, String estado, String txnId) {
    }
}
