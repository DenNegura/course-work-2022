package com.airmoldova.DAO;

import com.airmoldova.entity.Flight;
import com.airmoldova.entity.Ticket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class TicketDAO implements DAO <Ticket, Integer> {
    private final SessionFactory sessionFactory;

    public TicketDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Ticket ticket) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(ticket);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Ticket ticket) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(ticket);
            session.getTransaction().commit();
        }
    }

    @Override
    public Ticket read(Integer key) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final Ticket ticket = session.get(Ticket.class, key);
            session.getTransaction().commit();
            return ticket != null ? ticket : new Ticket();
        }
    }

    @Override
    public void delete(Ticket ticket) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(ticket);
            session.getTransaction().commit();
        }
    }

    public List<Ticket> getAll() {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            final List<Ticket> list = session.createQuery("from Ticket").list();
            session.getTransaction().commit();
            return list;
        }
    }
}
