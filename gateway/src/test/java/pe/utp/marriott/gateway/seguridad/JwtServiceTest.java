package pe.utp.marriott.gateway.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static SeguridadProperties props(String secreto, Duration duracion) {
        return new SeguridadProperties(secreto, duracion, "jw-marriott-lima", List.of());
    }

    private final JwtService jwt = new JwtService(props("secreto-de-prueba-con-mas-de-32-caracteres", Duration.ofHours(1)));

    @Test
    void unTokenEmitidoSeValidaConSusRoles() {
        String token = jwt.emitir("recepcion", List.of("ADMIN", "CLIENTE"));

        var sesion = jwt.validar(token);

        assertThat(sesion).isPresent();
        assertThat(sesion.get().usuario()).isEqualTo("recepcion");
        assertThat(sesion.get().tiene("ADMIN")).isTrue();
    }

    @Test
    void unTokenFirmadoConOtroSecretoNoSirve() {
        JwtService otro = new JwtService(props("otro-secreto-distinto-de-mas-de-32-caracteres", Duration.ofHours(1)));
        assertThat(jwt.validar(otro.emitir("intruso", List.of("ADMIN")))).isEmpty();
    }

    @Test
    void unTokenVencidoNoSirve() {
        JwtService corto = new JwtService(props("secreto-de-prueba-con-mas-de-32-caracteres", Duration.ofSeconds(-5)));
        assertThat(jwt.validar(corto.emitir("cliente", List.of("CLIENTE")))).isEmpty();
    }

    @Test
    void basuraNoSirve() {
        assertThat(jwt.validar("no-es-un-jwt")).isEmpty();
    }
}
