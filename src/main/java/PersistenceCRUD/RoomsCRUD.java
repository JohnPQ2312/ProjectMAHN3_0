package PersistenceCRUD;

import PersistenceClasses.Rooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class RoomsCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addRoom(Rooms room) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Rooms getRoomById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        Rooms room;
        try {
            room = em.find(Rooms.class, id);
        } finally {
            em.close();
        }
        return room;
    }

    public List<Rooms> getAllRooms() {
        EntityManager em = emf.createEntityManager();
        List<Rooms> rooms;
        try {
            rooms = em.createQuery("SELECT r FROM Rooms r", Rooms.class).getResultList();
        } finally {
            em.close();
        }
        return rooms;
    }

    public void updateRoom(Rooms room) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(room);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteRoom(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Rooms room = em.find(Rooms.class, id);
            if (room != null) {
                em.remove(room);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}