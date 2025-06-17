package PersistenceCRUD;

import PersistenceClasses.Entries;
import PersistenceClasses.Transfers;
import PersistenceClasses.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.Date;
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

    public void registerTransfer(Entries entry, Users fromUser, Users toUser) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            entry.getPurchases().setUsers(toUser);
            em.merge(entry);

            Transfers transfer = new Transfers();
            transfer.setEntries(entry);
            transfer.setFromUser(fromUser);
            transfer.setToUser(toUser);
            transfer.setTransferDate(new Date());
            em.persist(transfer);

            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
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
