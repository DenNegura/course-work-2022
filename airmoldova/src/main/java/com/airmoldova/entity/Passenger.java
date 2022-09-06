package com.airmoldova.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

@Entity
@Data
@ToString
@Table(name = "passenger")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passanger_seq")
    @SequenceGenerator(name = "passanger_seq", sequenceName = "passanger_id_pas_seq", allocationSize = 1)
    @Column(name = "id_pas")
    private int idPassenger;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "e_mail")
    private String eMail;

    @Column(name = "date_born")
    private LocalDate dateBorn;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "type_document")
    private TypeDocument typeDocument;

    @Column(name = "personal_code")
    private String personalCode;

    @Column(name = "document_code")
    private String documentCode;

    @Column(name = "country_doc")
    private String countryDoc;

    @Column(name = "date_exp")
    private LocalDate dateExp;

    @Column(name = "registration")
    private boolean registration;

    public String documentType() {
        return getTypeDocument().getTypeDocument();
    }
    public String getDateBornFormatDate() {
        return getDateBorn().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public void setDateBornFormatDate(String date) {
        setDateBorn(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE));
    }
    public String getDateExpFormatDate() {
        return getDateExp().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public void setDateExpFormatDate(String date) {
        setDateExp(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
