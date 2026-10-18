package pe.utp.marriott.pagos.pasarela;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PasarelaSimuladaTest {

    private final PasarelaSimulada pasarela = new PasarelaSimulada(new BigDecimal("20000"));

    @Test
    void apruebaUnTokenValido() {
        var r = pasarela.cobrar(new BigDecimal("3140.00"), "PEN", "tok_abc123");
        assertThat(r.aprobado()).isTrue();
        assertThat(r.txnId()).startsWith("txn_");
    }

    @Test
    void rechazaLosTokensDeRechazo() {
        assertThat(pasarela.cobrar(BigDecimal.TEN, "PEN", "tok_rechazo").aprobado()).isFalse();
        assertThat(pasarela.cobrar(BigDecimal.TEN, "PEN", "tok_sin_fondos").aprobado()).isFalse();
        assertThat(pasarela.cobrar(BigDecimal.TEN, "PEN", "4111111111111111").aprobado()).isFalse();
    }

    @Test
    void rechazaPorEncimaDelLimite() {
        assertThat(pasarela.cobrar(new BigDecimal("20000.01"), "PEN", "tok_abc123").aprobado()).isFalse();
    }
}
