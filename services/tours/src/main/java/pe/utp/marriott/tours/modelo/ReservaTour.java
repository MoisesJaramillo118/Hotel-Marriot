package pe.utp.marriott.tours.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reserva_tour", indexes = @Index(name = "ix_reserva_tour_reserva", columnList = "reserva_id"))
@Getter
@Setter
@NoArgsConstructor
public class ReservaTour {

    @Id
    @Column(length = 20)
    private String id;

    @Column(name = "tour_id", nullable = false, length = 20)
    private String tourId;

    // Referencias lógicas a los servicios Clientes y Reservas (sin FK física).
    @Column(name = "cliente_id", nullable = false, length = 20)
    private String clienteId;

    @Column(name = "reserva_id", length = 20)
    private String reservaId;

    @Column(nullable = false)
    private int cupos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoReservaTour estado;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
}
