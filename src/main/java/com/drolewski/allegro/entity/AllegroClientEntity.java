package com.drolewski.allegro.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Allegro_Client")
public class AllegroClientEntity {
    @Id
    private Integer id;

    @Column(name = "name_surname", insertable = false, updatable = false)
    private String nameSurname;

    @Column(insertable = false, updatable = false)
    private String nip;

    @Column(name = "company_Name")
    private String companyName;

    private String email;

    @Column(name = "phone_Number1")
    private String phoneNumber1;

    @Column(name = "phone_Number2")
    private String phoneNumber2;

    private String login;

    private String address;
}
