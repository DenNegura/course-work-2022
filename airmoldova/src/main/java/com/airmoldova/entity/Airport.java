package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "airport")
@Data
public class Airport {
    @Id
    @Column(name = "code_icao")
    private String codeICAO;

    @Column(name = "code_iata")
    private String codeIATA;

    @Column(name = "title")
    private String title;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String Country;
}
