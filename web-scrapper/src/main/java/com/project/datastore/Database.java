package com.project.datastore;

import javax.persistence.*;
import java.util.List;

public class Database {


    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("web-scraper");

    private static final Object lock = new Object();

    public static EntityManagerFactory getEntityManagerFactory() {
        synchronized (lock) {
            return emf;
        }
    }


    public static <T> void createOrUpdate(T item) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
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

    public static <T> List<T> getItems(Class<T> itemClass) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        String query = String.format("SELECT t FROM %s t", itemClass.getSimpleName());
        TypedQuery<T> tq = em.createQuery(query, itemClass);

        try {
            return tq.getResultList();
        } finally {
            em.close();
        }
    }

    public static <T> List<T> getItemsWhere(Class<T> itemClass, String where, Parameter[] params) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        String query = String.format("SELECT t FROM %s t WHERE %s", itemClass.getSimpleName(), where);
        TypedQuery<T> tq = em.createQuery(query, itemClass);

        for (var param : params) {
            tq.setParameter(param.key, param.value);
        }

        try {
            return tq.getResultList();
        } finally {
            em.close();
        }
    }

    public static class Parameter {
        final String key;
        final Object value;

        public Parameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

    }
}