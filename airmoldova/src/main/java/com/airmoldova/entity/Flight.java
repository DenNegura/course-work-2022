package com.airmoldova.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "flight")
@Data
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight_seq")
    @SequenceGenerator(name = "flight_seq", sequenceName = "flight_id_flight_seq", allocationSize = 1)
    @Column(name = "id_flight")
    private int idFlight;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "airport_dep")
    private Airport airportDep;

    @Column(name = "date_departure")
    private LocalDateTime dateDeparture;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "airport_arrival")
    private Airport airportArrival;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "airplane")
    private Airplane airplane;

    @Column(name = "duration")
    private LocalTime duration;

    @Column(name = "date_arrival")
    private LocalDateTime dateArrival;

    @Column(name = "distance")
    private int distance;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pricing_policy")
    private PricingPolicy pricingPolicy;

    public String getFormatDateDeparture() {
        return dateDeparture.getDayOfWeek() + "-" +
                dateDeparture.getDayOfMonth() + "-" +
                dateDeparture.getMonth() + " " +
                dateDeparture.toLocalTime();
    }
    public String getFormatDateArrival() {
        return dateArrival.getDayOfWeek() + "-" +
                dateArrival.getDayOfMonth() + "-" +
                dateArrival.getMonth() + " " +
                dateArrival.toLocalTime();
    }
    public String getFormatTime() {
        if(duration.getHour() != 0 && duration.getMinute() != 0) {
            return duration.getHour() + " hour " + duration.getMinute() + " min";
        }
        if(duration.getHour() != 0) {
            return duration.getHour() + " hour";
        }
        else {
            return duration.getMinute() + " min";
        }
    }
    public String getModelAirplane() {
        return airplane.getModel();
    }
    public String getTitleAirportArrival() {
        return airportArrival.getTitle();
    }
    public String getPositionAirportArrival() {
        return airportArrival.getCity() + " / " + airportArrival.getCountry();
    }
}
