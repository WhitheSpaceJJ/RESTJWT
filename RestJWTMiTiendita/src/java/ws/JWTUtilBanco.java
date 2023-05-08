package ws;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTUtilBanco {
   private static JWTUtilBanco instance;

    private JWTUtilBanco() {
    }
    
        public static JWTUtilBanco getIntance(){
            if(instance==null){
                instance=new JWTUtilBanco();
            }
            return instance;
        }
   
   
    private static final String ISSUER = "BancoMexico";
//    private static final String SECRET = "mySecretKey";
    private static final int EXPIRATION_MINUTES = 30;

    static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(Usuario usuario) {
             LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
          String token = Jwts.builder()
                .setSubject("acceso")
                .setExpiration(expirationDate)
                .setIssuer(ISSUER)
                .claim("usuario", new String[]{"user", usuario.getUsuario()})
                .signWith(KEY)
                .compact();
        return token;
    }

    public  boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

}
