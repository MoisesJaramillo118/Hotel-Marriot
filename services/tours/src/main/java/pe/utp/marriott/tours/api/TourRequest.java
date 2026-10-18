package pe.utp.marriott.tours.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TourRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotNull LocalDate fecha,
        @Min(1) int cupoTotal,
        @NotNull @DecimalMin(value = "0.01") BigDecimal precio,
        @Pattern(regexp = "^[A-Z]{3}$", message = "debe ser un código ISO de 3 letras") String moneda) {
}
