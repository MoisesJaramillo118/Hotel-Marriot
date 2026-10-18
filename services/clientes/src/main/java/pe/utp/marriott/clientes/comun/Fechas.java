package pe.utp.marriott.clientes.comun;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class Fechas {

    public static final ZoneId LIMA = ZoneId.of("America/Lima");

    private Fechas() {
    }

    public static OffsetDateTime ahora() {
        return OffsetDateTime.now(LIMA).withNano(0);
    }
}
