package com.drolewski.allegro.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="allegro_client_deduplicated")
public class DeduplicatedClientEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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
    private DeduplicatedClientEntity companyParent;

    @OneToOne
    @JoinColumn(name = "individual_parent")
    private DeduplicatedClientEntity individualParent;

    public DeduplicatedClientEntity(Integer allegroId, String nameSurname, String nip, String companyName,
                                    String email, String phoneNumber1, String phoneNumber2, String login,
                                    String address, DeduplicatedClientEntity companyParent,
                                    DeduplicatedClientEntity individualParent) {
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

    public DeduplicatedClientEntity() {
    }
}
