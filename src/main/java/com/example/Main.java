package com.example;

import com.example.services.CompetitorService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

    public static void main(String[] args) throws Exception {
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "9090";
        }

        Server server = new Server(Integer.valueOf(webPort));

        // Registrar recursos en Jersey
        ResourceConfig config = new ResourceConfig();
        config.register(CompetitorService.class);
        config.register(JacksonConfig.class);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new ServletContainer(config)), "/*");

        // Inicializar EntityManagerFactory (crea las tablas en Derby)
        PersistenceManager.getInstance().getEntityManagerFactory();

        server.setHandler(context);
        server.start();
        server.join();
    }
}
