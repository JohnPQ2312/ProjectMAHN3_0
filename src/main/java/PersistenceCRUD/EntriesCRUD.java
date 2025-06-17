package PersistenceCRUD;

import PersistenceClasses.Entries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class EntriesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addEntry(Entries entry) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entry);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Entries getEntryById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Entries entry;
        try {
            entry = em.find(Entries.class, id);
        } finally {
            em.close();
        }
        return entry;
    }

    public List<Entries> getAllEntries() {
        EntityManager em = emf.createEntityManager();
        List<Entries> list;
        try {
            list = em.createQuery("SELECT e FROM Entries e", Entries.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateEntry(Entries entry) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entry);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteEntry(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Entries entry = em.find(Entries.class, id);
            if (entry != null) {
                em.remove(entry);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Entries getEntryByQrCode(String qrCode) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Entries> result = em.createQuery(
                    "SELECT e FROM Entries e WHERE e.qrCode = :qrCode", Entries.class)
                    .setParameter("qrCode", qrCode)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public List<Entries> getEntriesByPurchaseId(BigDecimal purchaseId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Entries e WHERE e.purchases.id = :purchaseId", Entries.class)
                    .setParameter("purchaseId", purchaseId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
