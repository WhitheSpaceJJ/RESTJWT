/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTUtil {
    private static final String ISSUER = "BancoMexico";
    private static final String SECRET = "mySecretKey";
    private static final int EXPIRATION_MINUTES = 30;

    static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(Usuario usuario) {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(usuario.getUsuario())
                .setExpiration(expirationDate)
                .signWith(KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }
}