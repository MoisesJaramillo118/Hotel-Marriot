package pe.utp.marriott.tours.modelo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourRepository extends JpaRepository<Tour, String> {

    List<Tour> findByFechaAndCupoDisponibleGreaterThanOrderByNombre(LocalDate fecha, int cupo);

    List<Tour> findByFechaGreaterThanEqualOrderByFechaAscNombreAsc(LocalDate desde);

    /**
     * Descuenta el cupo en una sola sentencia atómica: si dos clientes piden el último cupo
     * a la vez, solo uno logra actualizar la fila y el otro recibe 0.
     */
    @Modifying
    @Query("update Tour t set t.cupoDisponible = t.cupoDisponible - :cupos where t.id = :id and t.cupoDisponible >= :cupos")
    int descontarCupo(@Param("id") String id, @Param("cupos") int cupos);

    @Modifying
    @Query("update Tour t set t.cupoDisponible = least(t.cupoTotal, t.cupoDisponible + :cupos) where t.id = :id")
    int devolverCupo(@Param("id") String id, @Param("cupos") int cupos);
}
