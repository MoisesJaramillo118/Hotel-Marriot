package pe.utp.marriott.reservas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reserva", indexes = @Index(name = "ix_reserva_cliente", columnList = "cliente_id"))
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    private static final String SEPARADOR = "\n";

    @Id
    @Column(length = 20)
    private String id;

    // Identificadores lógicos hacia otros servicios: no son claves foráneas físicas.
    @Column(name = "cliente_id", nullable = false, length = 20)
    private String clienteId;

    @Column(name = "habitacion_id", nullable = false, length = 20)
    private String habitacionId;

    @Column(name = "paquete_id", length = 20)
    private String paqueteId;

    @Column(name = "tour_id", length = 20)
    private String tourId;

    @Column(nullable = false)
    private LocalDate desde;

    @Column(nullable = false)
    private LocalDate hasta;

    @Column(nullable = false)
    private int noches;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Column(length = 1000)
    private String items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoReserva estado;

    @Column(name = "pago_txn_id", length = 40)
    private String pagoTxnId;

    @Column(name = "pago_estado", length = 15)
    private String pagoEstado;

    /** Por qué se canceló, si fue el propio flujo el que no pudo completarse. */
    @Column(length = 300)
    private String motivo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;

    public List<String> getListaItems() {
        return items == null || items.isEmpty() ? List.of() : Arrays.asList(items.split(SEPARADOR));
    }

    public void setListaItems(List<String> lista) {
        this.items = String.join(SEPARADOR, lista);
    }
}
