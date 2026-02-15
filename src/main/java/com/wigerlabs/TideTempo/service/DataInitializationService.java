package com.wigerlabs.TideTempo.service;

import com.wigerlabs.TideTempo.entity.Priority;
import com.wigerlabs.TideTempo.entity.Status;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class DataInitializationService {

    public static void initializeDefaultData() {
        initializeStatuses();
        initializePriorities();
    }

    private static void initializeStatuses() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Check if statuses already exist
            List<Status> existingStatuses = session.createQuery("FROM Status", Status.class).list();

            if (existingStatuses.isEmpty()) {
                // Create default statuses using native SQL
                LocalDateTime now = LocalDateTime.now();

                session.createNativeMutationQuery("INSERT INTO status (id, value) VALUES (:id, :value)")
                        .setParameter("id", 1)
                        .setParameter("value", Status.Type.COMPLETED.getValue())
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value) VALUES (:id, :value)")
                        .setParameter("id", 2)
                        .setParameter("value", Status.Type.ONGOING.getValue())
                        .executeUpdate();

                transaction.commit();
                System.out.println("Default status values initialized successfully.");
            } else {
                System.out.println("Status values already exist. Skipping initialization.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error initializing status values: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static void initializePriorities() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Check if Priorities already exist
            List<Priority> existingPriorities = session.createQuery("FROM Priority", Priority.class).list();

            if (existingPriorities.isEmpty()) {
                // Create default Priorities using native SQL
                LocalDateTime now = LocalDateTime.now();

                session.createNativeMutationQuery("INSERT INTO Priority (id, value) VALUES (:id, :value)")
                        .setParameter("id", 1)
                        .setParameter("value", Priority.Type.MEDIUM.getValue())
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO Priority (id, value) VALUES (:id, :value)")
                        .setParameter("id", 2)
                        .setParameter("value", Priority.Type.HIGH.getValue())
                        .executeUpdate();

                transaction.commit();
                System.out.println("Default priority values initialized successfully.");
            } else {
                System.out.println("Priority values already exist. Skipping initialization.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error initializing priority values: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}