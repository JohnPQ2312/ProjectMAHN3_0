package PersistenceCRUD;

import PersistenceClasses.Purchases;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class PurchasesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addPurchase(Purchases purchase) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(purchase);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Purchases getPurchaseById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Purchases purchase;
        try {
            purchase = em.find(Purchases.class, id);
        } finally {
            em.close();
        }
        return purchase;
    }

    public List<Purchases> getAllPurchases() {
        EntityManager em = emf.createEntityManager();
        List<Purchases> list;
        try {
            list = em.createQuery("SELECT p FROM Purchases p", Purchases.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updatePurchase(Purchases purchase) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(purchase);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deletePurchase(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Purchases purchase = em.find(Purchases.class, id);
            if (purchase != null) {
                em.remove(purchase);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}