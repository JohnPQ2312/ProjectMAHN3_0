package PersistenceCRUD;

import PersistenceClasses.RoomImages;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class RoomImagesCRUD {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addRoomImage(RoomImages roomImage) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(roomImage);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public RoomImages getRoomImageById(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        RoomImages roomImage;
        try {
            roomImage = em.find(RoomImages.class, id);
        } finally {
            em.close();
        }
        return roomImage;
    }

    public List<RoomImages> getAllRoomImages() {
        EntityManager em = emf.createEntityManager();
        List<RoomImages> list;
        try {
            list = em.createQuery("SELECT ri FROM RoomImages ri", RoomImages.class).getResultList();
        } finally {
            em.close();
        }
        return list;
    }

    public void updateRoomImage(RoomImages roomImage) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(roomImage);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteRoomImage(BigDecimal id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            RoomImages roomImage = em.find(RoomImages.class, id);
            if (roomImage != null) {
                em.remove(roomImage);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}