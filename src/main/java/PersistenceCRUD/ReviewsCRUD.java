package PersistenceCRUD;

import PersistenceClasses.Reviews;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class ReviewsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addReview(Reviews review) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(review);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Reviews getReviewById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Reviews review;
        try {
            review = em.find(Reviews.class, id);
        } finally {
            em.close();
        }
        return review;
    }

    public List<Reviews> getAllReviews() {
        EntityManager em = emf.createEntityManager();
        List<Reviews> list;
        try {
            list = em.createQuery("SELECT r FROM Reviews r", Reviews.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateReview(Reviews review) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(review);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteReview(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reviews review = em.find(Reviews.class, id);
            if (review != null) {
                em.remove(review);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}