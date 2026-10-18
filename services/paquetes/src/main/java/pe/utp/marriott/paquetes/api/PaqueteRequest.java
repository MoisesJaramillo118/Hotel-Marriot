package pe.utp.marriott.paquetes.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PaqueteRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 1000) String descripcion,
        @NotNull @DecimalMin(value = "0.01") BigDecimal precio,
        @Pattern(regexp = "^[A-Z]{3}$", message = "debe ser un código ISO de 3 letras") String moneda,
        Boolean activo) {
}
