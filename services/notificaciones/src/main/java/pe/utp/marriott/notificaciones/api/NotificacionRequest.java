package pe.utp.marriott.notificaciones.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import pe.utp.marriott.notificaciones.modelo.Canal;
import pe.utp.marriott.notificaciones.modelo.Plantilla;

public record NotificacionRequest(
        Canal canal,
        @NotBlank @Size(max = 150) String destinatario,
        @NotNull Plantilla plantilla,
        Map<String, String> datos) {
}
