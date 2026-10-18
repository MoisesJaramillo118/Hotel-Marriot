package pe.utp.marriott.pagos.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import pe.utp.marriott.pagos.modelo.MetodoPago;

public record PagoRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal monto,
        @Pattern(regexp = "^[A-Z]{3}$", message = "debe ser un código ISO de 3 letras") String moneda,
        @NotNull MetodoPago metodo,
        @NotBlank String tokenTarjeta,
        String referencia) {
}
