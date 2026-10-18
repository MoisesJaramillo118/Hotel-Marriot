package pe.utp.marriott.clientes.comun;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Genera los identificadores legibles del contrato (CLI-000123, RES-004521) con secuencias de PostgreSQL. */
@Component
public class Secuencias {

    private final JdbcTemplate jdbc;

    public Secuencias(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String siguiente(String secuencia, String prefijo, int digitos) {
        // El nombre de la secuencia es una constante del código, nunca una entrada del usuario.
        Long valor = jdbc.queryForObject("SELECT nextval('" + secuencia + "')", Long.class);
        return prefijo + "-" + String.format("%0" + digitos + "d", valor);
    }
}
