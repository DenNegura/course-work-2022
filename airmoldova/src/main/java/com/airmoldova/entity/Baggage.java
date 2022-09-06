package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "baggage")
public class Baggage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baggage_seq")
    @SequenceGenerator(name = "baggage_seq", sequenceName = "baggage_id_bag_seq", allocationSize = 1)
    @Column(name = "id_bag")
    private int idBaggage;

    @Column(name = "weight")
    private int weight;

    @Column(name = "additional_price")
    private double additionalPrice;
}
