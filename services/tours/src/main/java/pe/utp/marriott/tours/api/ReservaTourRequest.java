package pe.utp.marriott.tours.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservaTourRequest(@NotBlank String clienteId, String reservaId, @Min(1) @Max(20) Integer cupos) {

    public int cuposPedidos() {
        return cupos == null ? 1 : cupos;
    }
}
