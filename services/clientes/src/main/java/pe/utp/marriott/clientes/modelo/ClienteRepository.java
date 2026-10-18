package pe.utp.marriott.clientes.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    Optional<Cliente> findByDocTipoAndDocNumero(String docTipo, String docNumero);
}
