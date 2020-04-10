package com.drolewski.allegro.service;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;

import java.util.List;

public interface DeduplicatedClients {
    List<DeduplicatedClientsEntity> getAllDeduplicatedClients();
    List<DeduplicatedClientsEntity> getDeduplicatedClientsByNIP(String nip);
    List<DeduplicatedClientsEntity> importClients();
    void deduplicateCompanyClient(AllegroClientEntity clients);
    void updateAllegroId();
}
