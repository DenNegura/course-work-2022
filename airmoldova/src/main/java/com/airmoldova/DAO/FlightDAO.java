package com.airmoldova.DAO;

import com.airmoldova.entity.Flight;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class FlightDAO implements DAO<Flight, Integer> {

    private final SessionFactory sessionFactory;

    public FlightDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Flight flight) {
        try(Session session = sessionFactory.openSession()) {
             session.beginTransaction();
             session.save(flight);
             session.getTransaction().commit();
        }
    }

    @Override
    public void update(Flight flight) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(flight);
            session.getTransaction().commit();
        }
    }

    @Override
    public Flight read(Integer key) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final Flight flight = session.get(Flight.class, key);
            session.getTransaction().commit();
            return flight != null ? flight : new Flight();
        }
    }

    @Override
    public void delete(Flight flight) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(flight);
            session.getTransaction().commit();
        }
    }

    public List<Flight> getAll() {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final List<Flight> list = session.createQuery("from Flight").list();
            session.getTransaction().commit();
            return list;
        }
    }
}
