package pe.utp.marriott.habitaciones.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.habitaciones.api.BloqueoRequest;
import pe.utp.marriott.habitaciones.api.HabitacionRequest;
import pe.utp.marriott.habitaciones.comun.Fechas;
import pe.utp.marriott.habitaciones.comun.Secuencias;
import pe.utp.marriott.habitaciones.error.ApiException;
import pe.utp.marriott.habitaciones.modelo.Bloqueo;
import pe.utp.marriott.habitaciones.modelo.BloqueoRepository;
import pe.utp.marriott.habitaciones.modelo.EstadoBloqueo;
import pe.utp.marriott.habitaciones.modelo.EstadoHabitacion;
import pe.utp.marriott.habitaciones.modelo.Habitacion;
import pe.utp.marriott.habitaciones.modelo.HabitacionRepository;
import pe.utp.marriott.habitaciones.modelo.TipoHabitacion;

@Service
public class HabitacionService {

    private static final int MAX_SUGERENCIAS = 3;

    private final HabitacionRepository habitaciones;
    private final BloqueoRepository bloqueos;
    private final Secuencias secuencias;

    public HabitacionService(HabitacionRepository habitaciones, BloqueoRepository bloqueos, Secuencias secuencias) {
        this.habitaciones = habitaciones;
        this.bloqueos = bloqueos;
        this.secuencias = secuencias;
    }

    @Transactional(readOnly = true)
    public List<Habitacion> consultar(LocalDate desde, LocalDate hasta, TipoHabitacion tipo) {
        if (desde == null && hasta == null) {
            return habitaciones.porTipo(tipo);
        }
        validarRango(desde, hasta);
        return habitaciones.disponibles(desde, hasta, tipo);
    }

    @Transactional(readOnly = true)
    public Habitacion obtener(String id) {
        return habitaciones.findById(id).orElseThrow(() -> ApiException.noEncontrado("La habitación", id));
    }

    @Transactional
    public Habitacion crear(HabitacionRequest req) {
        if (habitaciones.existsById(req.id())) {
            throw ApiException.conflicto("HABITACION_DUPLICADA", "La habitación " + req.id() + " ya existe.");
        }
        Habitacion h = new Habitacion();
        h.setId(req.id());
        h.setTipo(req.tipo());
        h.setPiso(req.piso());
        h.setTarifaNoche(req.tarifaNoche());
        h.setMoneda(req.moneda() == null ? "PEN" : req.moneda());
        h.setEstado(req.estado() == null ? EstadoHabitacion.DISPONIBLE : req.estado());
        return habitaciones.save(h);
    }

    /**
     * Toma el cupo de la habitación para una reserva. Es idempotente: si la misma reserva
     * ya tiene el bloqueo, lo devuelve en vez de fallar (el orquestador puede reintentar).
     */
    @Transactional
    public Bloqueo bloquear(String habitacionId, BloqueoRequest req) {
        validarRango(req.desde(), req.hasta());
        Habitacion h = habitaciones.findByIdParaActualizar(habitacionId)
                .orElseThrow(() -> ApiException.noEncontrado("La habitación", habitacionId));

        List<Bloqueo> previos = bloqueos.findByReservaIdAndEstado(req.reservaId(), EstadoBloqueo.ACTIVO);
        for (Bloqueo b : previos) {
            if (b.getHabitacionId().equals(habitacionId) && b.getDesde().equals(req.desde())
                    && b.getHasta().equals(req.hasta())) {
                return b;
            }
        }

        if (h.getEstado() != EstadoHabitacion.DISPONIBLE || bloqueos.existeCruce(habitacionId, req.desde(), req.hasta())) {
            List<String> sugerencias = habitaciones.disponibles(req.desde(), req.hasta(), h.getTipo()).stream()
                    .map(Habitacion::getId)
                    .filter(id -> !id.equals(habitacionId))
                    .limit(MAX_SUGERENCIAS)
                    .toList();
            throw new ApiException(HttpStatus.CONFLICT, "SIN_DISPONIBILIDAD",
                    "La habitación " + habitacionId + " no tiene cupo en el rango solicitado.",
                    Map.of("sugerencias", sugerencias));
        }

        Bloqueo b = new Bloqueo();
        b.setId(secuencias.siguiente("seq_bloqueo", "BLQ", 6));
        b.setHabitacionId(habitacionId);
        b.setReservaId(req.reservaId());
        b.setDesde(req.desde());
        b.setHasta(req.hasta());
        b.setEstado(EstadoBloqueo.ACTIVO);
        b.setCreadoEn(Fechas.ahora());
        return bloqueos.save(b);
    }

    /** Libera el cupo de una reserva (cancelación o compensación). Idempotente. */
    @Transactional
    public int liberar(String reservaId) {
        List<Bloqueo> activos = bloqueos.findByReservaIdAndEstado(reservaId, EstadoBloqueo.ACTIVO);
        activos.forEach(b -> b.setEstado(EstadoBloqueo.LIBERADO));
        return activos.size();
    }

    private static void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw ApiException.solicitudInvalida("FECHAS_INVALIDAS", "Indique 'desde' y 'hasta' juntos.");
        }
        if (!desde.isBefore(hasta)) {
            throw ApiException.solicitudInvalida("FECHAS_INVALIDAS", "'desde' debe ser anterior a 'hasta'.");
        }
    }
}
