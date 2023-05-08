package ws;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("bancomexico")
public class BancoMexico {

    private static double tipoDeCambioPesoDolar = 0.0;
    private static double tipoDeCambioPesoEuro = 0.0;
    private ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();
    private static List<Usuario> usuarios = new ArrayList<>();

    public BancoMexico() {
        if (BancoMexico.usuarios.isEmpty()) {
              Random random = new Random();
            tipoDeCambioPesoDolar = 18 + random.nextDouble() * 5;
            tipoDeCambioPesoEuro = 21 + random.nextDouble() * 5;
            for (int i = 0; i < 10; i++) {
                usuarios.add(new Usuario("usuario" + i, "contrasena" + i));
            }
        }
    }

    

    public static boolean validarUsuario(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario get = usuarios.get(i);
            if (get.getContrasena().equalsIgnoreCase(usuario.getContrasena()) && get.getUsuario().equalsIgnoreCase(usuario.getUsuario())) {
                return true;
            }
        }
        return false;
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(Usuario usuario) {
        if (validarUsuario(usuario)) {
            String token = JWTUtilBanco.getIntance().generateToken(usuario);
            return token;
        } 
        else {
            return "Error de autenticación";
        }
    }

    @POST
    @Path(value = "tipo-cambio-peso-dolar")
    public void setTipoDeCambioPesoDolar(@Suspended final AsyncResponse asyncResponse, @HeaderParam(value = "Authorization") final String authorizationHeader, final double tipoCambio) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doSetTipoDeCambioPesoDolar(authorizationHeader, tipoCambio);
                asyncResponse.resume(javax.ws.rs.core.Response.ok().build());
            }
        });
    }

    private void doSetTipoDeCambioPesoDolar(@HeaderParam("Authorization") String authorizationHeader, double tipoCambio) {
//        if (!JWTUtilBanco.getIntance().validateToken(authorizationHeader)) {
//            throw new NotAuthorizedException("Token inválido");
//        }
        tipoDeCambioPesoDolar = tipoCambio;
    }

    @POST
    @Path(value = "tipo-cambio-peso-euro")
    public void setTipoDeCambioPesoEuro(@Suspended final AsyncResponse asyncResponse, @HeaderParam(value = "Authorization") final String authorizationHeader, final double tipoCambio) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doSetTipoDeCambioPesoEuro(authorizationHeader, tipoCambio);
                asyncResponse.resume(javax.ws.rs.core.Response.ok().build());
            }
        });
    }

    private void doSetTipoDeCambioPesoEuro(@HeaderParam("Authorization") String authorizationHeader, double tipoCambio) {
//        if (!JWTUtilBanco.getIntance().validateToken(authorizationHeader)) {
//            throw new NotAuthorizedException("Token inválido");
//        }
        tipoDeCambioPesoEuro = tipoCambio;
    }

    @GET
    @Path(value = "tipo-cambio-peso-euro")
    @Produces(value = MediaType.APPLICATION_JSON)
    public void getTipoDeCambioPesoEuro(@Suspended final AsyncResponse asyncResponse, @HeaderParam(value = "Authorization") final String authorizationHeader) {
        executorService.submit(new Runnable() {
            public void run() {
                asyncResponse.resume(doGetTipoDeCambioPesoEuro(authorizationHeader));
            }
        });
    }

    private String doGetTipoDeCambioPesoEuro(@HeaderParam("Authorization") String authorizationHeader) {
//        if (!JWTUtilBanco.getIntance().validateToken(authorizationHeader)) {
//            throw new NotAuthorizedException("Token inválido");
//        }
        return "{ \"tipoCambio\": " + tipoDeCambioPesoEuro + " }";
    }

    @GET
    @Path(value = "tipo-cambio-peso-dolar")
    @Produces(value = MediaType.APPLICATION_JSON)
    public void getTipoDeCambioPesoDolar(@Suspended final AsyncResponse asyncResponse, @HeaderParam(value = "Authorization") final String authorizationHeader) {
        executorService.submit(new Runnable() {
            public void run() {
                asyncResponse.resume(doGetTipoDeCambioPesoDolar(authorizationHeader));
            }
        });
    }

    private String doGetTipoDeCambioPesoDolar(@HeaderParam("Authorization") String authorizationHeader) {
//        if (!JWTUtilBanco.getIntance().validateToken(authorizationHeader)) {
//            throw new NotAuthorizedException("Token inválido");
//        }
        return "{ \"tipoCambioDolar\": " + tipoDeCambioPesoDolar + " }";
    }
}
