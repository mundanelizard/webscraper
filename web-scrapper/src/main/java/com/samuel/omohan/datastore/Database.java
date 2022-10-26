package com.samuel.omohan.datastore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


public class Database {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("web-scraper");

    public static <T> void createOrUpdate(T item) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(item);
            et.commit();
        } catch (Exception ex) {
            et.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public static List<Camera> getCameras() {
        EntityManager em = entityManagerFactory.createEntityManager();
        String query = "SELECT c FROM Camera c";

        TypedQuery<Camera> tq = em.createQuery(query, Camera.class);

        try {
            return tq.getResultList();
        } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println(ex.getMessage());
        } finally {
            em.close();
        }

        return new ArrayList<>();
    }

    public static List<Listing> getListings() {
        EntityManager em = entityManagerFactory.createEntityManager();
        String query = "SELECT l FROM Listing l";

        TypedQuery<Listing> tq = em.createQuery(query, Listing.class);

        try {
            return tq.getResultList();
        } finally {
            em.close();
        }
    }

    public static List<Listing> getListings(String qs) {
        EntityManager em = entityManagerFactory.createEntityManager();
        String query = "SELECT l FROM Listing l WHERE l.providerId = :provider";

        TypedQuery<Listing> tq = em.createQuery(query, Listing.class);
        tq.setParameter("provider", qs);

        try {
            return tq.getResultList();
        } finally {
            em.close();
        }
    }

    public static void closeEntityManager() {
        entityManagerFactory.close();
    }
}
