package ws;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Provider
public class JwtFilterBanco implements ContainerRequestFilter {

//    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
//    private static final String JWT_SECRET = "mysecretkey"; 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String metodo = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        if (!path.contains("login") && !metodo.equals("POST") && !path.contains("bancomexico/login")) {
            String token = requestContext.getHeaderString("Autorizacion");
            if (token != null) {
                if (!JWTUtilBanco.getIntance().validateToken(token)) {
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                }
            } else {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }
    }
}
