package pe.utp.marriott.reservas.modelo;

import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SolicitudIdempotenteRepository extends JpaRepository<SolicitudIdempotente, String> {

    /** Reserva la clave de forma atómica: devuelve 1 si la tomó esta solicitud, 0 si otra ya la tenía. */
    @Transactional
    @Modifying
    @Query(value = """
            insert into solicitud_idempotente (clave, hash_solicitud, estado, creado_en)
            values (:clave, :hash, 'EN_PROCESO', :ahora)
            on conflict (clave) do nothing
            """, nativeQuery = true)
    int tomar(@Param("clave") String clave, @Param("hash") String hash, @Param("ahora") OffsetDateTime ahora);
}
