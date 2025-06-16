package PersistenceCRUD;

import PersistenceClasses.Users;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

public class UsersCRUD {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void addUser(Users user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Users> getAllUsers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Users getUserById(BigDecimal userId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, userId);
        } finally {
            em.close();
        }
    }

    public Users getUserByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Users> query = em.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Users> getUsersByRole(String role) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Users u WHERE u.role = :role", Users.class)
                     .setParameter("role", role)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Users> searchUsersByName(String nameFragment) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Users u WHERE LOWER(u.name) LIKE :name", Users.class)
                     .setParameter("name", "%" + nameFragment.toLowerCase() + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Users getUserByCredentials(String email, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Users> query = em.createQuery(
                "SELECT u FROM Users u WHERE u.email = :email AND u.password = :password", Users.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void updateUser(Users user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteUser(BigDecimal userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Users user = em.find(Users.class, userId);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
