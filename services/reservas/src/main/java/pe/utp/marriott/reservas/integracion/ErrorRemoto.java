package pe.utp.marriott.reservas.integracion;

import java.util.Map;

/** Un servicio respondió 4xx. El orquestador decide qué significa para la reserva. */
public class ErrorRemoto extends RuntimeException {

    private final String servicio;
    private final int status;
    private final Map<String, Object> cuerpo;

    public ErrorRemoto(String servicio, int status, Map<String, Object> cuerpo) {
        super(servicio + " respondió " + status + ": " + cuerpo);
        this.servicio = servicio;
        this.status = status;
        this.cuerpo = cuerpo;
    }

    public String getServicio() {
        return servicio;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, Object> getCuerpo() {
        return cuerpo;
    }

    public String codigo() {
        Object codigo = cuerpo.get("error");
        return codigo == null ? "ERROR_REMOTO" : codigo.toString();
    }

    public String mensaje() {
        Object mensaje = cuerpo.get("mensaje");
        return mensaje == null ? getMessage() : mensaje.toString();
    }
}
