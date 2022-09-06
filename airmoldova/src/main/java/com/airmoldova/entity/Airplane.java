package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "airplane")
@Data
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "airplane_seq")
    @SequenceGenerator(name = "airplane_seq", sequenceName = "airplane_id_airplane_seq", allocationSize = 1)
    @Column(name = "id_airplane")
    private int idAirplane;

    @Column(name = "model")
    private String model;

    @Column(name = "economy")
    private int economyClass;

    @Column(name = "comfort")
    private int comfortClass;

    @Column(name = "business")
    private int businessClass;
}
