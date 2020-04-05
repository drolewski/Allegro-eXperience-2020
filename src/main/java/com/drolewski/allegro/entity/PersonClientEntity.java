package com.drolewski.allegro.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "person")
public class PersonClientEntity {
    @Id
    private Integer id;

    @OneToMany(mappedBy = "owner")
    private List<AllegroClientEntity> allegroClientEntityList;
}
