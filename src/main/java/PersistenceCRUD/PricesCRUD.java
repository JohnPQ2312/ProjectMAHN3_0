package PersistenceCRUD;

import PersistenceClasses.Prices;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class PricesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addPrice(Prices price) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(price);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Prices getPriceById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Prices price;
        try {
            price = em.find(Prices.class, id);
        } finally {
            em.close();
        }
        return price;
    }

    public List<Prices> getAllPrices() {
        EntityManager em = emf.createEntityManager();
        List<Prices> list;
        try {
            list = em.createQuery("SELECT p FROM Prices p", Prices.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updatePrice(Prices price) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(price);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deletePrice(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Prices price = em.find(Prices.class, id);
            if (price != null) {
                em.remove(price);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}