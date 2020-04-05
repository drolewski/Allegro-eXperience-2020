package com.drolewski.allegro.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
public class AllegroClientEntity {
    @Id
    private Integer id;

    private String name;

    private String surname;

    private String nip;

    private String companyName;

    private String email;

    private String phoneNumber1;

    private String phoneNumber2;

    private String login;

    private String address;
}
