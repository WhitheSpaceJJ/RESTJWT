/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ws;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.util.logging.Logger;

@Provider
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    private static final String JWT_SECRET = "mysecretkey"; // Cambiar esto por una clave segura en producción

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Obtener el token JWT de la cabecera Authorization
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        String token = authorizationHeader.substring("Bearer".length()).trim();

        // Verificar y decodificar el token
        try {
            Key key = JWTUtil.KEY; // Utiliza una clave simétrica en este ejemplo
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            requestContext.setProperty("username", username); // Almacenar el nombre de usuario en la propiedad del contexto para su posterior uso
        } catch (Exception e) {
            logger.warning("JWT no válido: " + e.getMessage());
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}