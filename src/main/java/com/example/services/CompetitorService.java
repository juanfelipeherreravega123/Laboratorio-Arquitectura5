package com.example.services;

import com.example.PersistenceManager;
import com.example.dto.CompetitorDTO;
import com.example.dto.LoginDTO;
import com.example.models.Competitor;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/competitors")
public class CompetitorService {

    @PersistenceContext(unitName = "CompetitorsPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance()
                    .getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureEntityManager() {
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = PersistenceManager.getInstance()
                    .getEntityManagerFactory().createEntityManager();
        }
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        ensureEntityManager();
        Query q = entityManager.createQuery(
                "select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(competitors)
                .build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        ensureEntityManager();
        JSONObject rta = new JSONObject();
        Competitor competitorTmp = new Competitor();
        competitorTmp.setAddress(competitor.getAddress());
        competitorTmp.setAge(competitor.getAge());
        competitorTmp.setCellphone(competitor.getCellphone());
        competitorTmp.setCity(competitor.getCity());
        competitorTmp.setCountry(competitor.getCountry());
        competitorTmp.setName(competitor.getName());
        competitorTmp.setSurname(competitor.getSurname());
        competitorTmp.setTelephone(competitor.getTelephone());
        competitorTmp.setEmail(competitor.getEmail());
        competitorTmp.setPassword(competitor.getPassword());

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competitorTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competitorTmp);
            rta.put("competitor_id", competitorTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competitorTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(rta.toString())
                .build();
    }

    // Actividad: servicio de login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials) {
        ensureEntityManager();
        try {
            Query q = entityManager.createQuery(
                    "select c from Competitor c where c.email = :email and c.password = :password");
            q.setParameter("email", credentials.getEmail());
            q.setParameter("password", credentials.getPassword());
            Competitor competitor = (Competitor) q.getSingleResult();
            return Response.status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(competitor)
                    .build();
        } catch (NoResultException e) {
            throw new NotAuthorizedException("Invalid email or password",
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
