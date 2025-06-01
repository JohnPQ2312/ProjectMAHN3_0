package PersistenceCRUD;

import PersistenceClasses.Commissions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class CommissionsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addCommission(Commissions commission) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(commission);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Commissions getCommissionById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Commissions comm;
        try {
            comm = em.find(Commissions.class, id);
        } finally {
            em.close();
        }
        return comm;
    }

    public List<Commissions> getAllCommissions() {
        EntityManager em = emf.createEntityManager();
        List<Commissions> list;
        try {
            list = em.createQuery("SELECT c FROM Commissions c", Commissions.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateCommission(Commissions commission) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(commission);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteCommission(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Commissions comm = em.find(Commissions.class, id);
            if (comm != null) {
                em.remove(comm);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}