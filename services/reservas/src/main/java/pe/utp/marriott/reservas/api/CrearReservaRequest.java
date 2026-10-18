package pe.utp.marriott.reservas.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record CrearReservaRequest(
        @NotBlank String clienteId,
        @NotBlank String habitacionId,
        @NotNull LocalDate desde,
        @NotNull LocalDate hasta,
        String tourId,
        String paqueteId,
        @NotNull @Valid DatosPago pago) {

    // "tour_id": "" significa lo mismo que no pedir tour.
    public CrearReservaRequest {
        tourId = tourId == null || tourId.isBlank() ? null : tourId.trim();
        paqueteId = paqueteId == null || paqueteId.isBlank() ? null : paqueteId.trim();
    }

    public record DatosPago(
            @NotBlank @Pattern(regexp = "TARJETA|YAPE|TRANSFERENCIA", message = "debe ser TARJETA, YAPE o TRANSFERENCIA")
            String metodo,
            @NotBlank String tokenTarjeta) {
    }
}
