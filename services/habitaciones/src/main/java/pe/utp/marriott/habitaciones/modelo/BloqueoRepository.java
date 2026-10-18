package pe.utp.marriott.habitaciones.modelo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloqueoRepository extends JpaRepository<Bloqueo, String> {

    // Dos rangos [desde, hasta) se cruzan si cada uno empieza antes de que termine el otro:
    // el día de salida de un huésped puede ser el de llegada del siguiente.
    @Query("""
            select count(b) > 0 from Bloqueo b
            where b.habitacionId = :habitacionId
              and b.estado = pe.utp.marriott.habitaciones.modelo.EstadoBloqueo.ACTIVO
              and b.desde < :hasta and b.hasta > :desde
            """)
    boolean existeCruce(@Param("habitacionId") String habitacionId, @Param("desde") LocalDate desde,
                        @Param("hasta") LocalDate hasta);

    List<Bloqueo> findByReservaIdAndEstado(String reservaId, EstadoBloqueo estado);
}
