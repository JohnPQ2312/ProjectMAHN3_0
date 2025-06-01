package PersistenceCRUD;

import PersistenceClasses.Themes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class ThemesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addTheme(Themes theme) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(theme);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Themes getThemeById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Themes theme;
        try {
            theme = em.find(Themes.class, id);
        } finally {
            em.close();
        }
        return theme;
    }

    public List<Themes> getAllThemes() {
        EntityManager em = emf.createEntityManager();
        List<Themes> list;
        try {
            list = em.createQuery("SELECT t FROM Themes t", Themes.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateTheme(Themes theme) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(theme);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteTheme(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Themes theme = em.find(Themes.class, id);
            if (theme != null) {
                em.remove(theme);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}