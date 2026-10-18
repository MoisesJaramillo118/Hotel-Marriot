package pe.utp.marriott.pagos.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, String> {

    Optional<Pago> findByTxnId(String txnId);
}
