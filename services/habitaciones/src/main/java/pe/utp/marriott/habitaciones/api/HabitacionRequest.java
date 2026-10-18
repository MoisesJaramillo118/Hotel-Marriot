package pe.utp.marriott.habitaciones.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import pe.utp.marriott.habitaciones.modelo.EstadoHabitacion;
import pe.utp.marriott.habitaciones.modelo.TipoHabitacion;

public record HabitacionRequest(
        @NotBlank @Pattern(regexp = "^HAB-[0-9]{3,4}$", message = "debe tener el formato HAB-301") String id,
        @NotNull TipoHabitacion tipo,
        @Min(1) int piso,
        @NotNull @DecimalMin(value = "0.01") BigDecimal tarifaNoche,
        @Pattern(regexp = "^[A-Z]{3}$", message = "debe ser un código ISO de 3 letras") String moneda,
        EstadoHabitacion estado) {
}
