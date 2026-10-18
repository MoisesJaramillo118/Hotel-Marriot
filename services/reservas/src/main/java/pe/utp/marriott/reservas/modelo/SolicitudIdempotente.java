package pe.utp.marriott.reservas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lo que se respondió a cada Idempotency-Key. Si el cliente reintenta (se cortó la red,
 * pulsó dos veces), recibe la misma respuesta y no se crea una segunda reserva ni un segundo cobro.
 */
@Entity
@Table(name = "solicitud_idempotente")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudIdempotente {

    public static final String EN_PROCESO = "EN_PROCESO";
    public static final String COMPLETADA = "COMPLETADA";

    @Id
    @Column(length = 100)
    private String clave;

    @Column(name = "hash_solicitud", nullable = false, length = 64)
    private String hashSolicitud;

    @Column(nullable = false, length = 15)
    private String estado;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(columnDefinition = "text")
    private String cuerpo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
}
