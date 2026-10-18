package pe.utp.marriott.notificaciones.api;

import java.time.OffsetDateTime;
import pe.utp.marriott.notificaciones.modelo.Canal;
import pe.utp.marriott.notificaciones.modelo.EstadoNotificacion;
import pe.utp.marriott.notificaciones.modelo.Notificacion;
import pe.utp.marriott.notificaciones.modelo.Plantilla;

public record NotificacionResponse(String id, Canal canal, String destinatario, Plantilla plantilla, String asunto,
                                   String mensaje, EstadoNotificacion estado, OffsetDateTime creadoEn,
                                   OffsetDateTime enviadoEn) {

    public static NotificacionResponse de(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getCanal(), n.getDestinatario(), n.getPlantilla(), n.getAsunto(),
                n.getMensaje(), n.getEstado(), n.getCreadoEn(), n.getEnviadoEn());
    }
}
