package pe.utp.marriott.pagos.pasarela;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Pasarela de prueba para el entorno académico (no sale a internet). Reglas:
 * <ul>
 *   <li>token que empieza con "tok_rechazo" → rechazado (tarjeta rechazada);</li>
 *   <li>token "tok_sin_fondos" o monto mayor al límite → rechazado por fondos insuficientes;</li>
 *   <li>cualquier otro token "tok_..." → aprobado.</li>
 * </ul>
 */
@Component
public class PasarelaSimulada implements PasarelaPagos {

    private final BigDecimal limite;

    public PasarelaSimulada(@Value("${pasarela.limite-por-cobro:20000}") BigDecimal limite) {
        this.limite = limite;
    }

    @Override
    public ResultadoCobro cobrar(BigDecimal monto, String moneda, String token) {
        if (token == null || !token.startsWith("tok_")) {
            return new ResultadoCobro(false, null, "Token de tarjeta inválido.");
        }
        if (token.startsWith("tok_rechazo")) {
            return new ResultadoCobro(false, null, "La entidad emisora rechazó la tarjeta.");
        }
        if (token.equals("tok_sin_fondos") || monto.compareTo(limite) > 0) {
            return new ResultadoCobro(false, null, "Fondos insuficientes.");
        }
        String txn = "txn_" + ThreadLocalRandom.current().nextInt(10_000, 100_000_000);
        return new ResultadoCobro(true, txn, null);
    }

    @Override
    public boolean reembolsar(String txnId, BigDecimal monto) {
        return true;
    }
}
