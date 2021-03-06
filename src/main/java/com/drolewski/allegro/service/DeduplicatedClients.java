package com.drolewski.allegro.service;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientEntity;

import java.util.List;

public interface DeduplicatedClients {
    List<DeduplicatedClientEntity> getAllDeduplicatedClients();

    List<DeduplicatedClientEntity> getDeduplicatedClientsByNIP(String nip);

    List<DeduplicatedClientEntity> getDeduplicatedClientsByNameSurname(String name);

    List<DeduplicatedClientEntity> importClients();
}
