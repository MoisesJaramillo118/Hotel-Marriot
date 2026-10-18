package pe.utp.marriott.notificaciones.modelo;

import java.util.Map;

/** Plantillas de mensaje. Los marcadores {clave} se reemplazan con los 'datos' de la solicitud. */
public enum Plantilla {

    BIENVENIDA("Bienvenido a JW Marriott Lima",
            "Hola {nombre}, gracias por registrarte. Ya puedes reservar habitaciones, paquetes y tours."),
    RESERVA_CONFIRMADA("Tu reserva {reserva_id} está confirmada",
            "Hola {nombre}, confirmamos tu reserva {reserva_id} del {desde} al {hasta}. Total: {moneda} {total}."),
    RESERVA_CANCELADA("Tu reserva {reserva_id} fue cancelada",
            "Hola {nombre}, cancelamos tu reserva {reserva_id}. Si hubo un cobro, se reembolsará al mismo medio de pago.");

    private final String asunto;
    private final String cuerpo;

    Plantilla(String asunto, String cuerpo) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    public String asunto(Map<String, String> datos) {
        return rellenar(asunto, datos);
    }

    public String cuerpo(Map<String, String> datos) {
        return rellenar(cuerpo, datos);
    }

    private static String rellenar(String texto, Map<String, String> datos) {
        String resultado = texto;
        for (Map.Entry<String, String> e : datos.entrySet()) {
            resultado = resultado.replace("{" + e.getKey() + "}", String.valueOf(e.getValue()));
        }
        return resultado;
    }
}
