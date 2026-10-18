package pe.utp.marriott.habitaciones.modelo;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitacionRepository extends JpaRepository<Habitacion, String> {

    /** Bloquea la fila de la habitación para que dos reservas simultáneas no tomen las mismas noches. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Habitacion h where h.id = :id")
    Optional<Habitacion> findByIdParaActualizar(@Param("id") String id);

    @Query("""
            select h from Habitacion h
            where h.estado = pe.utp.marriott.habitaciones.modelo.EstadoHabitacion.DISPONIBLE
              and (:tipo is null or h.tipo = :tipo)
              and not exists (
                  select 1 from Bloqueo b
                  where b.habitacionId = h.id
                    and b.estado = pe.utp.marriott.habitaciones.modelo.EstadoBloqueo.ACTIVO
                    and b.desde < :hasta and b.hasta > :desde)
            order by h.tarifaNoche, h.id
            """)
    List<Habitacion> disponibles(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta,
                                 @Param("tipo") TipoHabitacion tipo);

    @Query("select h from Habitacion h where (:tipo is null or h.tipo = :tipo) order by h.id")
    List<Habitacion> porTipo(@Param("tipo") TipoHabitacion tipo);
}
