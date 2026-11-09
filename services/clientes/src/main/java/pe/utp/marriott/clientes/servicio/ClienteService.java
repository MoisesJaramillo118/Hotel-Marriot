package pe.utp.marriott.clientes.servicio;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.clientes.api.ClienteRequest;
import pe.utp.marriott.clientes.comun.Fechas;
import pe.utp.marriott.clientes.comun.Secuencias;
import pe.utp.marriott.clientes.error.ApiException;
import pe.utp.marriott.clientes.modelo.Cliente;
import pe.utp.marriott.clientes.modelo.ClienteRepository;
import pe.utp.marriott.clientes.modelo.EstadoCliente;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final Secuencias secuencias;

    public ClienteService(ClienteRepository repo, Secuencias secuencias) {
        this.repo = repo;
        this.secuencias = secuencias;
    }

    @Transactional
    public Cliente registrar(ClienteRequest req) {
        validarUnicidad(req, null);
        Cliente c = new Cliente();
        c.setId(secuencias.siguiente("seq_cliente", "CLI", 6));
        copiar(req, c);
        c.setEstado(EstadoCliente.ACTIVO);
        c.setCreadoEn(Fechas.ahora());
        return repo.save(c);
    }

    @Transactional(readOnly = true)
    public Cliente obtener(String id) {
        return repo.findById(id).orElseThrow(() -> ApiException.noEncontrado("El cliente", id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(String email) {
        if (email != null && !email.isBlank()) {
            return repo.findByEmailIgnoreCase(email.trim()).map(List::of).orElse(List.of());
        }
        return repo.findAll();
    }

    @Transactional
    public Cliente actualizar(String id, ClienteRequest req) {
        Cliente c = obtener(id);
        validarUnicidad(req, id);
        copiar(req, c);
        return c;
    }

    /** El titular activa o desactiva el consentimiento para compartir su contacto con partners. */
    @Transactional
    public Cliente actualizarConsentimiento(String id, boolean comparte) {
        Cliente c = obtener(id);
        c.setComparteDatos(comparte);
        return c;
    }

    /**
     * Busca un cliente por email para entregárselo a un hotel asociado. Solo responde si el
     * cliente existe Y autorizó compartir sus datos; en cualquier otro caso devuelve 404, para
     * no revelar a un tercero si una persona está o no registrada ni si dio o no su consentimiento.
     */
    @Transactional(readOnly = true)
    public Cliente obtenerCompartiblePorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw ApiException.solicitudInvalida("EMAIL_REQUERIDO", "Debe indicar el email del cliente.");
        }
        return repo.findByEmailIgnoreCase(email.trim())
                .filter(Cliente::isComparteDatos)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NO_COMPARTIBLE",
                        "No hay un cliente que haya autorizado compartir sus datos con ese email."));
    }

    private void validarUnicidad(ClienteRequest req, String idActual) {
        repo.findByEmailIgnoreCase(req.email().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("EMAIL_DUPLICADO", "Ya existe un cliente con el correo " + req.email() + ".");
                });
        repo.findByDocTipoAndDocNumero(req.documento().tipo(), req.documento().numero().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("DOCUMENTO_DUPLICADO",
                            "Ya existe un cliente con el documento " + req.documento().tipo() + " " + req.documento().numero() + ".");
                });
    }

    private static void copiar(ClienteRequest req, Cliente c) {
        c.setNombres(req.nombres().trim());
        c.setApellidos(req.apellidos().trim());
        c.setEmail(req.email().trim().toLowerCase());
        c.setDocTipo(req.documento().tipo());
        c.setDocNumero(req.documento().numero().trim());
        c.setTelefono(req.telefono());
    }
}
