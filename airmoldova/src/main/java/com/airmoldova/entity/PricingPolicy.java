package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "pricing_policy")
public class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_seq")
    @SequenceGenerator(name = "policy_seq", sequenceName = "pricing_policy_id_policy_seq", allocationSize = 1)
    @Column(name = "id_policy")
    private int idPolicy;

    @Column(name = "economy")
    private double priceEconomy;

    @Column(name = "comfort")
    private double priceComfort;

    @Column(name = "business")
    private double priceBusiness;

    @Column(name = "kg_baggage")
    private double priceKgBaggage;

    @Column(name = "hand_luggage")
    private int handLuggage;

    @Column(name = "free_baggage")
    private int freeBaggage;
}
