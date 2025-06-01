package PersistenceCRUD;

import PersistenceClasses.PaymentMethods;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class PaymentMethodsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addPaymentMethod(PaymentMethods pm) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pm);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public PaymentMethods getPaymentMethodById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        PaymentMethods pm;
        try {
            pm = em.find(PaymentMethods.class, id);
        } finally {
            em.close();
        }
        return pm;
    }

    public List<PaymentMethods> getAllPaymentMethods() {
        EntityManager em = emf.createEntityManager();
        List<PaymentMethods> list;
        try {
            list = em.createQuery("SELECT p FROM PaymentMethods p", PaymentMethods.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updatePaymentMethod(PaymentMethods pm) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pm);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deletePaymentMethod(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            PaymentMethods pm = em.find(PaymentMethods.class, id);
            if (pm != null) {
                em.remove(pm);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}