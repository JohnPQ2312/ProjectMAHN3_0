package PersistenceCRUD;
import PersistenceClasses.Museums;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;


public class MuseumsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addMuseum(Museums museum) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(museum);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Museums getMuseumById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Museums museum;
        try {
            museum = em.find(Museums.class, id);
        } finally {
            em.close();
        }
        return museum;
    }

    public List<Museums> getAllMuseums() {
        EntityManager em = emf.createEntityManager();
        List<Museums> museums;
        try {
            museums = em.createQuery("SELECT m FROM Museums m", Museums.class).getResultList();
        } finally {
            em.close();
        }
        return museums;
    }

    public void updateMuseum(Museums museum) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(museum);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteMuseum(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Museums museum = em.find(Museums.class, id);
            if (museum != null) {
                em.remove(museum);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}