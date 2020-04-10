package com.drolewski.allegro.service;

import com.drolewski.allegro.entity.DeduplicatedClientsEntity;

import java.util.List;

public interface DeduplicatedClients {
    List<DeduplicatedClientsEntity> getAllDeduplicatedClients();
    List<DeduplicatedClientsEntity> getDeduplicatedClientsByNIP(String nip);
    void saveDeduplicatedClients(List<DeduplicatedClientsEntity> deduplicatedClients);
}
