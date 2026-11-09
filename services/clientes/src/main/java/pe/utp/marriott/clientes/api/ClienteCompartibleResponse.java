package pe.utp.marriott.clientes.api;

import pe.utp.marriott.clientes.modelo.Cliente;

/**
 * Vista mínima del cliente que se comparte con un hotel asociado (partner), previo
 * consentimiento del titular. Expone SOLO contacto básico: nunca el documento de
 * identidad, el estado ni la fecha de registro. Es el contrato de interoperabilidad
 * entre sistemas de distintos hoteles.
 */
public record ClienteCompartibleResponse(
        String nombres,
        String apellidos,
        String email,
        String telefono,
        String origen) {

    public static ClienteCompartibleResponse de(Cliente c) {
        return new ClienteCompartibleResponse(
                c.getNombres(),
                c.getApellidos(),
                c.getEmail(),
                c.getTelefono(),
                "jw-marriott-lima");
    }
}
