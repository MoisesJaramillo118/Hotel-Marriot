package pe.utp.marriott.tours.servicio;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.marriott.tours.api.ReservaTourRequest;
import pe.utp.marriott.tours.api.TourRequest;
import pe.utp.marriott.tours.comun.Fechas;
import pe.utp.marriott.tours.comun.Secuencias;
import pe.utp.marriott.tours.error.ApiException;
import pe.utp.marriott.tours.modelo.EstadoReservaTour;
import pe.utp.marriott.tours.modelo.ReservaTour;
import pe.utp.marriott.tours.modelo.ReservaTourRepository;
import pe.utp.marriott.tours.modelo.Tour;
import pe.utp.marriott.tours.modelo.TourRepository;

@Service
public class TourService {

    private final TourRepository tours;
    private final ReservaTourRepository reservas;
    private final Secuencias secuencias;

    public TourService(TourRepository tours, ReservaTourRepository reservas, Secuencias secuencias) {
        this.tours = tours;
        this.reservas = reservas;
        this.secuencias = secuencias;
    }

    /** Con fecha: los tours de ese día que aún tienen cupo. Sin fecha: los próximos. */
    @Transactional(readOnly = true)
    public List<Tour> buscar(LocalDate fecha) {
        if (fecha != null) {
            return tours.findByFechaAndCupoDisponibleGreaterThanOrderByNombre(fecha, 0);
        }
        return tours.findByFechaGreaterThanEqualOrderByFechaAscNombreAsc(Fechas.ahora().toLocalDate());
    }

    @Transactional(readOnly = true)
    public Tour obtener(String id) {
        return tours.findById(id).orElseThrow(() -> ApiException.noEncontrado("El tour", id));
    }

    @Transactional
    public Tour crear(TourRequest req) {
        Tour t = new Tour();
        t.setId(secuencias.siguiente("seq_tour", "TOU", 3));
        t.setNombre(req.nombre().trim());
        t.setFecha(req.fecha());
        t.setCupoTotal(req.cupoTotal());
        t.setCupoDisponible(req.cupoTotal());
        t.setPrecio(req.precio());
        t.setMoneda(req.moneda() == null ? "PEN" : req.moneda());
        return tours.save(t);
    }

    @Transactional
    public ReservaTour reservar(String tourId, ReservaTourRequest req) {
        Tour tour = obtener(tourId);
        if (req.reservaId() != null) {
            var previa = reservas.findFirstByTourIdAndReservaIdAndEstado(tourId, req.reservaId(),
                    EstadoReservaTour.CONFIRMADA);
            if (previa.isPresent()) {
                return previa.get();
            }
        }
        if (tour.getFecha().isBefore(Fechas.ahora().toLocalDate())) {
            throw ApiException.conflicto("TOUR_PASADO", "El tour " + tourId + " ya se realizó.");
        }
        if (tours.descontarCupo(tourId, req.cuposPedidos()) == 0) {
            throw ApiException.conflicto("SIN_CUPO",
                    "El tour " + tourId + " no tiene " + req.cuposPedidos() + " cupo(s) disponible(s).");
        }
        ReservaTour r = new ReservaTour();
        r.setId(secuencias.siguiente("seq_reserva_tour", "RTO", 6));
        r.setTourId(tourId);
        r.setClienteId(req.clienteId());
        r.setReservaId(req.reservaId());
        r.setCupos(req.cuposPedidos());
        r.setEstado(EstadoReservaTour.CONFIRMADA);
        r.setCreadoEn(Fechas.ahora());
        return reservas.save(r);
    }

    /** Cancela los tours de una reserva integrada y devuelve sus cupos. Idempotente. */
    @Transactional
    public int cancelarPorReserva(String reservaId) {
        List<ReservaTour> activas = reservas.findByReservaIdAndEstado(reservaId, EstadoReservaTour.CONFIRMADA);
        for (ReservaTour r : activas) {
            r.setEstado(EstadoReservaTour.CANCELADA);
            tours.devolverCupo(r.getTourId(), r.getCupos());
        }
        return activas.size();
    }
}
