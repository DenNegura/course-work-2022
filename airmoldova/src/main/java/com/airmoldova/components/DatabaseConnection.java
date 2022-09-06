package com.airmoldova.components;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class DatabaseConnection {
    private SessionFactory sessionFactory = null;
    private Configuration configuration = null;
    private final String url;
    private final String username;
    private final String password;
    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public void openSessionFactory() {
        configuration = new Configuration();
        Properties properties = new Properties();
        properties.put(Environment.URL, url);
        properties.put(Environment.USER, username);
        properties.put(Environment.PASS, password);
        configuration.setProperties(properties);
    }
    public void addAnnotatedClass(Class annotatedClass) {
        configuration.addAnnotatedClass(annotatedClass);
    }
    public void buildSessionFactory() {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    public Session openSession() {
        return sessionFactory.openSession();
    }
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
