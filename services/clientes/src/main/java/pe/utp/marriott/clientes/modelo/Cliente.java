package pe.utp.marriott.clientes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cliente_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_cliente_documento", columnNames = {"doc_tipo", "doc_numero"})
})
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "doc_tipo", nullable = false, length = 15)
    private String docTipo;

    @Column(name = "doc_numero", nullable = false, length = 20)
    private String docNumero;

    @Column(length = 25)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCliente estado;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    // Consentimiento del cliente para compartir su contacto básico con hoteles asociados (partners).
    // Por defecto NO comparte: el cliente debe activarlo explícitamente.
    @Column(name = "comparte_datos", nullable = false)
    private boolean comparteDatos = false;
}
