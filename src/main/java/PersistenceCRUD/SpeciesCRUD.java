package PersistenceCRUD;

import PersistenceClasses.Species;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class SpeciesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addSpecies(Species species) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(species);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Species getSpeciesById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Species species;
        try {
            species = em.find(Species.class, id);
        } finally {
            em.close();
        }
        return species;
    }

    public List<Species> getAllSpecies() {
        EntityManager em = emf.createEntityManager();
        List<Species> list;
        try {
            list = em.createQuery("SELECT s FROM Species s", Species.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateSpecies(Species species) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(species);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteSpecies(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Species species = em.find(Species.class, id);
            if (species != null) {
                em.remove(species);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}