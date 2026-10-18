package pe.utp.marriott.gateway.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

class ReglasDeAccesoTest {

    @Test
    void elLoginYLaSaludSonPublicos() {
        assertThat(ReglasDeAcceso.esPublica("/api/v1/auth/token")).isTrue();
        assertThat(ReglasDeAcceso.esPublica("/actuator/health")).isTrue();
        assertThat(ReglasDeAcceso.esPublica("/api/v1/reservas")).isFalse();
    }

    @Test
    void elClienteLeeElCatalogoYReservaPeroNoLoModifica() {
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.GET, "/api/v1/habitaciones")).isNull();
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.GET, "/api/v1/tours/TOU-012")).isNull();
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/tours/TOU-012/reserva")).isNull();
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/reservas")).isNull();
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/clientes")).isNull();
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/paquetes")).isEqualTo("ADMIN");
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/habitaciones/HAB-301/bloqueo")).isEqualTo("ADMIN");
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.DELETE, "/api/v1/tours/reservas/RES-1")).isEqualTo("ADMIN");
    }

    @Test
    void lasUtilidadesInternasSonDelPersonal() {
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.POST, "/api/v1/pagos")).isEqualTo("ADMIN");
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.GET, "/api/v1/notificaciones/NOT-000001")).isEqualTo("ADMIN");
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.GET, "/api/v1/clientes")).isEqualTo("ADMIN");
        assertThat(ReglasDeAcceso.rolRequerido(HttpMethod.GET, "/api/v1/clientes/CLI-000123")).isNull();
    }
}
