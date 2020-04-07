package com.drolewski.allegro.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="allegro_client_deduplicated")
public class DeduplicatedClientsEntity extends AllegroClientEntity{
    @ManyToOne
    @JoinColumn(name = "id")
    @Column(name = "company_parent")
    private Integer companyParentId;

    @ManyToOne
    @JoinColumn(name = "id")
    @Column(name = "individual_parent")
    private Integer individualParentId;
}
