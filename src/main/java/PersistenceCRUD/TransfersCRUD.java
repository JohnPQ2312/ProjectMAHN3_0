package PersistenceCRUD;

import PersistenceClasses.Transfers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class TransfersCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addTransfer(Transfers transfer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(transfer);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Transfers getTransferById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Transfers transfer;
        try {
            transfer = em.find(Transfers.class, id);
        } finally {
            em.close();
        }
        return transfer;
    }

    public List<Transfers> getAllTransfers() {
        EntityManager em = emf.createEntityManager();
        List<Transfers> list;
        try {
            list = em.createQuery("SELECT t FROM Transfers t", Transfers.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateTransfer(Transfers transfer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(transfer);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteTransfer(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Transfers transfer = em.find(Transfers.class, id);
            if (transfer != null) {
                em.remove(transfer);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}