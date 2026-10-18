package pe.utp.marriott.habitaciones.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BloqueoRequest(@NotBlank String reservaId, @NotNull LocalDate desde, @NotNull LocalDate hasta) {
}
