/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PersistenceCRUD;
import PersistenceClasses.Users;

import jakarta.persistence.*;
import java.util.List;

public class UsersCRUD {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MuseumPersistence");

    public void addUser(Users user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Users> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        List<Users> users;
        try {
            users = em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
        } finally {
            em.close();
        }
        return users;
    }

    public Users getUserById(Long userId) {
        EntityManager em = emf.createEntityManager();
        Users user;
        try {
            user = em.find(Users.class, userId);
        } finally {
            em.close();
        }
        return user;
    }

    public void updateUser(Users user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteUser(Long userId) {
        EntityManager em = emf.createEntityManager();
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
