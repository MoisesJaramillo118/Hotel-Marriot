package pe.utp.marriott.pagos.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pago", uniqueConstraints = @UniqueConstraint(name = "uk_pago_txn", columnNames = "txn_id"))
@Getter
@Setter
@NoArgsConstructor
public class Pago {

    @Id
    @Column(length = 20)
    private String id;

    @Column(name = "txn_id", length = 40)
    private String txnId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    // Se guardan solo los últimos 4 caracteres del token: nunca el token completo.
    @Column(name = "token_final", length = 4)
    private String tokenFinal;

    /** Lo que se cobra (p. ej. el id de la reserva). Referencia lógica. */
    @Column(length = 40)
    private String referencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoPago estado;

    @Column(length = 200)
    private String motivo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
}
