package com.drolewski.allegro.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="allegro_client_deduplicated")
public class DeduplicatedClientsEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer allegroId;

    @Column(name = "name_surname")
    private String nameSurname;

    @Column
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

    @OneToOne
    @JoinColumn(name = "company_parent")
    private DeduplicatedClientsEntity companyParent;

    @OneToOne
    @JoinColumn(name = "individual_parent")
    private DeduplicatedClientsEntity individualParent;

    public DeduplicatedClientsEntity(Integer allegroId, String nameSurname, String nip, String companyName,
                                     String email, String phoneNumber1, String phoneNumber2, String login,
                                     String address, DeduplicatedClientsEntity companyParent,
                                     DeduplicatedClientsEntity individualParent) {
        this.allegroId = allegroId;
        this.nameSurname = nameSurname;
        this.nip = nip;
        this.companyName = companyName;
        this.email = email;
        this.phoneNumber1 = phoneNumber1;
        this.phoneNumber2 = phoneNumber2;
        this.login = login;
        this.address = address;
        this.companyParent = companyParent;
        this.individualParent = individualParent;
    }
}
