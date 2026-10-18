package pe.utp.marriott.habitaciones.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Reserva temporal de cupo: mientras está ACTIVO, nadie más puede tomar esas noches. */
@Entity
@Table(name = "bloqueo", indexes = {
        @Index(name = "ix_bloqueo_habitacion", columnList = "habitacion_id, estado"),
        @Index(name = "ix_bloqueo_reserva", columnList = "reserva_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Bloqueo {

    @Id
    @Column(length = 20)
    private String id;

    // Referencias lógicas: la habitación es de este servicio; la reserva vive en db_reservas.
    @Column(name = "habitacion_id", nullable = false, length = 20)
    private String habitacionId;

    @Column(name = "reserva_id", nullable = false, length = 20)
    private String reservaId;

    @Column(nullable = false)
    private LocalDate desde;

    @Column(nullable = false)
    private LocalDate hasta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoBloqueo estado;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
}
