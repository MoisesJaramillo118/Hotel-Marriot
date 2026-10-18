package pe.utp.marriott.notificaciones.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.notificaciones.comun.Fechas;
import pe.utp.marriott.notificaciones.modelo.EstadoNotificacion;
import pe.utp.marriott.notificaciones.modelo.NotificacionRepository;

/** Envía fuera del hilo de la solicitud: quien notifica recibe 202 sin esperar al proveedor. */
@Component
public class DespachadorNotificaciones {

    private static final Logger log = LoggerFactory.getLogger(DespachadorNotificaciones.class);

    private final NotificacionRepository repo;
    private final ProveedorMensajeria proveedor;

    public DespachadorNotificaciones(NotificacionRepository repo, ProveedorMensajeria proveedor) {
        this.repo = repo;
        this.proveedor = proveedor;
    }

    @Async
    @Transactional
    public void despachar(String id) {
        repo.findById(id).ifPresent(n -> {
            try {
                proveedor.enviar(n);
                n.setEstado(EstadoNotificacion.ENVIADA);
                n.setEnviadoEn(Fechas.ahora());
            } catch (RuntimeException ex) {
                log.warn("No se pudo enviar la notificación {}", id, ex);
                n.setEstado(EstadoNotificacion.FALLIDA);
            }
        });
    }
}
