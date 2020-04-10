package com.drolewski.allegro.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="allegro_client_deduplicated")
public class DeduplicatedClientsEntity{
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

    @OneToOne
    @JoinColumn(name = "company_parent")
    private DeduplicatedClientsEntity companyParent;

    @OneToOne
    @JoinColumn(name = "individual_parent")
    private DeduplicatedClientsEntity individualParent;
}
