package pe.utp.marriott.pagos.pasarela;

import java.math.BigDecimal;

/** Puerto hacia la pasarela externa. Cambiar de proveedor es cambiar esta implementación. */
public interface PasarelaPagos {

    ResultadoCobro cobrar(BigDecimal monto, String moneda, String token);

    boolean reembolsar(String txnId, BigDecimal monto);

    record ResultadoCobro(boolean aprobado, String txnId, String motivo) {
    }
}
