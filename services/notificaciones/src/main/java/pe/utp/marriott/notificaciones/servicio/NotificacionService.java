package pe.utp.marriott.notificaciones.servicio;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pe.utp.marriott.notificaciones.api.NotificacionRequest;
import pe.utp.marriott.notificaciones.comun.Fechas;
import pe.utp.marriott.notificaciones.comun.Secuencias;
import pe.utp.marriott.notificaciones.error.ApiException;
import pe.utp.marriott.notificaciones.modelo.Canal;
import pe.utp.marriott.notificaciones.modelo.EstadoNotificacion;
import pe.utp.marriott.notificaciones.modelo.Notificacion;
import pe.utp.marriott.notificaciones.modelo.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;
    private final DespachadorNotificaciones despachador;
    private final Secuencias secuencias;

    public NotificacionService(NotificacionRepository repo, DespachadorNotificaciones despachador,
                               Secuencias secuencias) {
        this.repo = repo;
        this.despachador = despachador;
        this.secuencias = secuencias;
    }

    @Transactional
    public Notificacion encolar(NotificacionRequest req) {
        Map<String, String> datos = req.datos() == null ? Map.of() : req.datos();
        Notificacion n = new Notificacion();
        n.setId(secuencias.siguiente("seq_notificacion", "NOT", 6));
        n.setCanal(req.canal() == null ? Canal.EMAIL : req.canal());
        n.setDestinatario(req.destinatario().trim());
        n.setPlantilla(req.plantilla());
        n.setAsunto(req.plantilla().asunto(datos));
        n.setMensaje(req.plantilla().cuerpo(datos));
        n.setEstado(EstadoNotificacion.ENCOLADA);
        n.setCreadoEn(Fechas.ahora());
        repo.save(n);
        // Se despacha recién cuando la fila existe en la base, para que el hilo asíncrono la encuentre.
        String id = n.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                despachador.despachar(id);
            }
        });
        return n;
    }

    @Transactional(readOnly = true)
    public Notificacion obtener(String id) {
        return repo.findById(id).orElseThrow(() -> ApiException.noEncontrado("La notificación", id));
    }
}
