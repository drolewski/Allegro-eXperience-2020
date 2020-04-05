package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllegroClientRepository extends JpaRepository<AllegroClientEntity, Integer> {
    List<AllegroClientEntity> findAll();
}
