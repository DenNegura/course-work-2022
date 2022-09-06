package com.airmoldova.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "type_document")
@Data
public class TypeDocument {
    @Id
    @Column(name = "id_type_doc")
    private int idType;

    @Column(name = "type_document")
    private String typeDocument;
}
