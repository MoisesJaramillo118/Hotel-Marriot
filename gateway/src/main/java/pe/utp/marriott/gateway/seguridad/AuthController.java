package pe.utp.marriott.gateway.seguridad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Emite el JWT que luego se envía como "Authorization: Bearer <token>". */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SeguridadProperties props;
    private final JwtService jwt;

    public AuthController(SeguridadProperties props, JwtService jwt) {
        this.props = props;
        this.jwt = jwt;
    }

    public record Credenciales(@NotBlank String usuario, @NotBlank String clave) {
    }

    public record Token(String accessToken, String tokenType, long expiraEn, String usuario, List<String> roles) {
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@Valid @RequestBody Credenciales cred) {
        return props.usuarios().stream()
                .filter(u -> u.usuario().equals(cred.usuario()) && iguales(u.clave(), cred.clave()))
                .findFirst()
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        new Token(jwt.emitir(u.usuario(), u.roles()), "Bearer", jwt.segundosDeVida(), u.usuario(),
                                u.roles())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "CREDENCIALES_INVALIDAS", "mensaje", "Usuario o clave incorrectos.")));
    }

    // Comparación en tiempo constante, para no revelar por el tiempo cuántos caracteres coinciden.
    private static boolean iguales(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
