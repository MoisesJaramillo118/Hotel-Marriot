package pe.utp.marriott.notificaciones.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.utp.marriott.notificaciones.modelo.Notificacion;

/**
 * Proveedor de correo/SMS simulado: deja el mensaje en el registro del contenedor.
 * Integrar uno real (SES, Brevo, Twilio) es reemplazar este componente.
 */
@Component
public class ProveedorMensajeria {

    private static final Logger log = LoggerFactory.getLogger(ProveedorMensajeria.class);

    public void enviar(Notificacion n) {
        log.info("[{}] a {} | {} | {}", n.getCanal(), n.getDestinatario(), n.getAsunto(), n.getMensaje());
    }
}
