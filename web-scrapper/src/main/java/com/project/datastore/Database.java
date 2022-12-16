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

    /**
     * Inserts or update a row in table of type T.class
     * @param item a java bean object
     * @param <T> a java bean class
     */
    public static <T> void createOrUpdate(T item) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.merge(item);
            et.commit();
        } catch (Exception ex) {
            System.out.println(item);
            et.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Gets all items in the table
     * @param itemClass a java bean class
     * @return a list of java bean instances
     * @param <T> a java bean class type
     */
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

    /**
     * Get all times from a java bean with a where clause.
     * @param itemClass a java bean class
     * @param where a where query for java clause.
     * @param params parameter to the where clause
     * @return  a list of java bean objects
     * @param <T> a java bean class.
     */
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

    /**
     * Parameters the getItemsWhere method. It's a key value pair store.
     */
    public static class Parameter {
        final String key;
        final Object value;

        public Parameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

    }
}