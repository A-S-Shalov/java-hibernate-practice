package com.example.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Утилита для работы с Hibernate SessionFactory.
 *
 * ВАЖНО ДЛЯ ТЕСТОВ:
 * 1) SessionFactory создаётся "лениво" (когда впервые вызвали getSessionFactory()).
 *    Это нужно, чтобы интеграционные тесты успели подставить настройки подключения
 *    к базе данных Testcontainers до создания SessionFactory.
 *
 * 2) Настройки подключения можно переопределить через System.setProperty(...):
 *    - hibernate.connection.url
 *    - hibernate.connection.username
 *    - hibernate.connection.password
 */
public final class HibernateUtil {

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Берём стандартный конфиг (hibernate.cfg.xml)
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");

            // Если тесты подставили настройки в System properties — применяем их
            overrideIfPresent(cfg, "hibernate.connection.url");
            overrideIfPresent(cfg, "hibernate.connection.username");
            overrideIfPresent(cfg, "hibernate.connection.password");

            return cfg.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Не удалось создать SessionFactory: " + e);
        }
    }

    private static void overrideIfPresent(Configuration cfg, String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            cfg.setProperty(key, value);
        }
    }

    public static SessionFactory getSessionFactory() {
        SessionFactory local = sessionFactory;
        if (local == null) {
            synchronized (HibernateUtil.class) {
                local = sessionFactory;
                if (local == null) {
                    sessionFactory = local = buildSessionFactory();
                }
            }
        }
        return local;
    }

    public static void shutdown() {
        SessionFactory local = sessionFactory;
        if (local != null) {
            local.close();
            sessionFactory = null;
        }
    }
}
