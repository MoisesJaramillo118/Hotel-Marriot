package pe.utp.marriott.gateway.seguridad;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de seguridad del gateway. Los usuarios son de demostración para el entorno
 * académico; en un despliegue real vendrían de un proveedor de identidad.
 */
@ConfigurationProperties(prefix = "seguridad")
public record SeguridadProperties(String jwtSecreto, Duration jwtDuracion, String emisor, List<Usuario> usuarios) {

    public record Usuario(String usuario, String clave, List<String> roles) {
    }
}
