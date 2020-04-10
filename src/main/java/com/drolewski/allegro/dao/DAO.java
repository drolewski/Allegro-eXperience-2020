package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.DeduplicatedClientsEntity;

import java.util.List;

public interface DAO<T> {
    void saveDeduplicated(DeduplicatedClientsEntity deduplicatedClientsEntity);
    List<DeduplicatedClientsEntity> getClientsByNameSurname(String nameSurname);
    List<DeduplicatedClientsEntity> getClientsByNIP(String nip);
    DeduplicatedClientsEntity getClientByEmail(String email);
    DeduplicatedClientsEntity getClientById(Integer id);
    DeduplicatedClientsEntity getClientByLogin(String login);
    List<DeduplicatedClientsEntity> getDeduplicatedClients();
}
