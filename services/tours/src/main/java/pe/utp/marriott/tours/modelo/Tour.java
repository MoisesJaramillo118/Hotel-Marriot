package pe.utp.marriott.tours.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tour")
@Getter
@Setter
@NoArgsConstructor
public class Tour {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "cupo_total", nullable = false)
    private int cupoTotal;

    @Column(name = "cupo_disponible", nullable = false)
    private int cupoDisponible;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false, length = 3)
    private String moneda;
}
