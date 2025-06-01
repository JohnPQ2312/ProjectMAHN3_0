package PersistenceCRUD;

import PersistenceClasses.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class CollectionsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addCollection(Collections collection) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(collection);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Collections getCollectionById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Collections collection;
        try {
            collection = em.find(Collections.class, id);
        } finally {
            em.close();
        }
        return collection;
    }

    public List<Collections> getAllCollections() {
        EntityManager em = emf.createEntityManager();
        List<Collections> collections;
        try {
            collections = em.createQuery("SELECT c FROM Collections c", Collections.class).getResultList();
        } finally {
            em.close();
        }
        return collections;
    }

    public void updateCollection(Collections collection) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(collection);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteCollection(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Collections collection = em.find(Collections.class, id);
            if (collection != null) {
                em.remove(collection);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}