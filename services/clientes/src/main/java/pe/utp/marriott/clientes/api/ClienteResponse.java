package pe.utp.marriott.clientes.api;

import java.time.OffsetDateTime;
import pe.utp.marriott.clientes.modelo.Cliente;
import pe.utp.marriott.clientes.modelo.EstadoCliente;

public record ClienteResponse(
        String id,
        String nombres,
        String apellidos,
        String email,
        ClienteRequest.Documento documento,
        String telefono,
        EstadoCliente estado,
        OffsetDateTime creadoEn) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombres(), c.getApellidos(), c.getEmail(),
                new ClienteRequest.Documento(c.getDocTipo(), c.getDocNumero()), c.getTelefono(), c.getEstado(),
                c.getCreadoEn());
    }
}
