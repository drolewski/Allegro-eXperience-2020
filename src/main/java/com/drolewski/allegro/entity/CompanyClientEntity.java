package com.drolewski.allegro.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name="company")
public class CompanyClientEntity {
    @Id
    private Integer id;

    @OneToMany(mappedBy = "company")
    private List<AllegroClientEntity> allegroClient;
}
