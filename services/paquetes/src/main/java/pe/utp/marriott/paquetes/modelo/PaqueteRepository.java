package pe.utp.marriott.paquetes.modelo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaqueteRepository extends JpaRepository<Paquete, String> {

    @Query("""
            select p from Paquete p
            where (:activo is null or p.activo = :activo)
              and (:precioMax is null or p.precio <= :precioMax)
              and (:texto is null or lower(p.nombre) like lower(concat('%', cast(:texto as string), '%')))
            order by p.precio, p.id
            """)
    List<Paquete> buscar(@Param("activo") Boolean activo, @Param("precioMax") BigDecimal precioMax,
                         @Param("texto") String texto);
}
