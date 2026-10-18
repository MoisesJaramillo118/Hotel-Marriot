package pe.utp.marriott.clientes.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull @Valid Documento documento,
        @Pattern(regexp = "^\\+?[0-9 ]{6,20}$", message = "debe ser un teléfono válido") String telefono) {

    public record Documento(
            @NotBlank @Pattern(regexp = "DNI|CE|PASAPORTE|RUC", message = "debe ser DNI, CE, PASAPORTE o RUC") String tipo,
            @NotBlank @Size(max = 20) String numero) {
    }
}
