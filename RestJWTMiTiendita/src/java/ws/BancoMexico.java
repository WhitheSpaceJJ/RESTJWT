/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package ws;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

@Path("bancomexico")
public class BancoMexico {

    private static double tipoDeCambioPesoDolar = 0.0;
    private static double tipoDeCambioPesoEuro = 0.0;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(Usuario usuario) {
        if (usuario.getUsuario().equals("usuario1") && usuario.getContrasena().equals("contrasena1")) {
            String token = JWTUtil.generateToken(usuario);
            return token;
        } else {
            return "Error de autenticación";
        }
    }

 
    @GET
    @Path("tipo-cambio-peso-dolar")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTipoDeCambioPesoDolar(@HeaderParam("Authorization") String authorizationHeader) {
         if (!JWTUtil.validateToken(authorizationHeader)) {
            throw new NotAuthorizedException("Token inválido");
        }
        return "{ \"tipoCambio\": " + tipoDeCambioPesoDolar + " }";
    }


    static {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(1);
        long initialDelay = LocalDateTime.now().until(midnight, ChronoUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
            // Genera valores aleatorios para los tipos de cambio
            Random random = new Random();
            tipoDeCambioPesoDolar = 18 + random.nextDouble() * 5;
            tipoDeCambioPesoEuro = 21 + random.nextDouble() * 5;
        }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }
    private ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

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
        if (!JWTUtil.validateToken(authorizationHeader)) {
            throw new NotAuthorizedException("Token inválido");
        }
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
        if (!JWTUtil.validateToken(authorizationHeader)) {
            throw new NotAuthorizedException("Token inválido");
        }
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
        if (!JWTUtil.validateToken(authorizationHeader)) {
            throw new NotAuthorizedException("Token inválido");
        }
        return "{ \"tipoCambio\": " + tipoDeCambioPesoEuro + " }";
    }
}
