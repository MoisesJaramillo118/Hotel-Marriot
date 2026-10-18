package pe.utp.marriott.gateway.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/** Emite y valida los JWT (HS256). El estado de la sesión viaja en el token: el gateway es stateless. */
@Component
public class JwtService {

    private final SecretKey llave;
    private final SeguridadProperties props;

    public JwtService(SeguridadProperties props) {
        byte[] secreto = props.jwtSecreto().getBytes(StandardCharsets.UTF_8);
        if (secreto.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres.");
        }
        this.llave = Keys.hmacShaKeyFor(secreto);
        this.props = props;
    }

    public String emitir(String usuario, List<String> roles) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .issuer(props.emisor())
                .subject(usuario)
                .claim("roles", roles)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(props.jwtDuracion())))
                .signWith(llave)
                .compact();
    }

    public Optional<Sesion> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(llave)
                    .requireIssuer(props.emisor())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            return Optional.of(new Sesion(claims.getSubject(), roles == null ? List.of() : roles));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long segundosDeVida() {
        return props.jwtDuracion().toSeconds();
    }

    public record Sesion(String usuario, List<String> roles) {

        public boolean tiene(String rol) {
            return roles.contains(rol);
        }
    }
}
