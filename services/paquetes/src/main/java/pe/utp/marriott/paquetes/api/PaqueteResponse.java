package pe.utp.marriott.paquetes.api;

import java.math.BigDecimal;
import pe.utp.marriott.paquetes.modelo.Paquete;

public record PaqueteResponse(String id, String nombre, String descripcion, BigDecimal precio, String moneda,
                              boolean activo) {

    public static PaqueteResponse de(Paquete p) {
        return new PaqueteResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getMoneda(),
                p.isActivo());
    }
}
