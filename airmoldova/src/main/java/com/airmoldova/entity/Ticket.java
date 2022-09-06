package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq")
    @SequenceGenerator(name = "ticket_seq", sequenceName = "ticket_id_ticket_seq", allocationSize = 1)
    @Column(name = "id_ticket")
    private int idTicket;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_passanger")
    private Passenger passenger;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_baggage")
    private Baggage baggage;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_flight")
    private Flight flight;

    @Column(name = "class")
    private String typeClass;

    @Column(name = "price")
    private double price;

    @Column(name = "seat")
    private int seat;

    @Column(name = "date_sale")
    private LocalDateTime dateSale;
}
