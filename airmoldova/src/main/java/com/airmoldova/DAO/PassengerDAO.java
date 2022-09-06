package com.airmoldova.DAO;

import com.airmoldova.entity.Flight;
import com.airmoldova.entity.Passenger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.core.parameters.P;

import java.util.List;

public class PassengerDAO implements DAO<Passenger, Integer> {
    private final SessionFactory sessionFactory;

    public PassengerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Passenger passenger) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(passenger);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Passenger passenger) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(passenger);
            session.getTransaction().commit();
        }
    }

    @Override
    public Passenger read(Integer key) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final Passenger passenger = session.get(Passenger.class, key);
            session.getTransaction().commit();
            return passenger != null ? passenger : new Passenger();
        }
    }

    @Override
    public void delete(Passenger passenger) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(passenger);
            session.getTransaction().commit();
        }
    }
    public List<Passenger> getAll(Boolean registration) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final List<Passenger> list = session.createQuery("from Passenger where id_pas = get_id_current_passenger(id_pas, " + registration + ")").list();
            session.getTransaction().commit();
            return list;
        }
    }
}
