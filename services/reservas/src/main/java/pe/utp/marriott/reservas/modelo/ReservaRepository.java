package pe.utp.marriott.reservas.modelo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, String> {

    List<Reserva> findByClienteIdOrderByCreadoEnDesc(String clienteId);
}
