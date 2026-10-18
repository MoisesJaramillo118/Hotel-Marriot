package pe.utp.marriott.tours.modelo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaTourRepository extends JpaRepository<ReservaTour, String> {

    Optional<ReservaTour> findFirstByTourIdAndReservaIdAndEstado(String tourId, String reservaId,
                                                                 EstadoReservaTour estado);

    List<ReservaTour> findByReservaIdAndEstado(String reservaId, EstadoReservaTour estado);
}
