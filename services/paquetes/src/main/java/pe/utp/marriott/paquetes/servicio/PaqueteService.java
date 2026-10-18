package pe.utp.marriott.paquetes.servicio;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.paquetes.api.PaqueteRequest;
import pe.utp.marriott.paquetes.comun.Secuencias;
import pe.utp.marriott.paquetes.error.ApiException;
import pe.utp.marriott.paquetes.modelo.Paquete;
import pe.utp.marriott.paquetes.modelo.PaqueteRepository;

@Service
public class PaqueteService {

    private final PaqueteRepository repo;
    private final Secuencias secuencias;

    public PaqueteService(PaqueteRepository repo, Secuencias secuencias) {
        this.repo = repo;
        this.secuencias = secuencias;
    }

    @Transactional(readOnly = true)
    public List<Paquete> buscar(Boolean activo, BigDecimal precioMax, String texto) {
        String filtro = texto == null || texto.isBlank() ? null : texto.trim();
        return repo.buscar(activo, precioMax, filtro);
    }

    @Transactional(readOnly = true)
    public Paquete obtener(String id) {
        return repo.findById(id).orElseThrow(() -> ApiException.noEncontrado("El paquete", id));
    }

    @Transactional
    public Paquete crear(PaqueteRequest req) {
        Paquete p = new Paquete();
        p.setId(secuencias.siguiente("seq_paquete", "PAQ", 3));
        copiar(req, p);
        return repo.save(p);
    }

    @Transactional
    public Paquete actualizar(String id, PaqueteRequest req) {
        Paquete p = obtener(id);
        copiar(req, p);
        return p;
    }

    /** Un paquete no se borra (reservas pasadas lo referencian): se desactiva. */
    @Transactional
    public Paquete desactivar(String id) {
        Paquete p = obtener(id);
        p.setActivo(false);
        return p;
    }

    private static void copiar(PaqueteRequest req, Paquete p) {
        p.setNombre(req.nombre().trim());
        p.setDescripcion(req.descripcion());
        p.setPrecio(req.precio());
        p.setMoneda(req.moneda() == null ? "PEN" : req.moneda());
        p.setActivo(req.activo() == null || req.activo());
    }
}
