package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;

import java.sql.SQLDataException;
import java.util.List;

public interface DAO<T> {
    void saveDeduplicated(DeduplicatedClientsEntity deduplicatedClientsEntity);
    List<DeduplicatedClientsEntity> getClientsByNameSurname(String nameSurname);
    List<DeduplicatedClientsEntity> getClientsByNIP(String nip);
    DeduplicatedClientsEntity getClientByEmail(String email);
    DeduplicatedClientsEntity getClientById(Integer id);
    List<DeduplicatedClientsEntity> getClientsByLogin(String login);
    List<DeduplicatedClientsEntity> getDeduplicatedClients();

    boolean isCompanyClientExist(AllegroClientEntity client);

    void updateOrAddClient(AllegroClientEntity client);

    void addNewCRMClient(AllegroClientEntity client) throws SQLDataException;

    List<DeduplicatedClientsEntity> getAccountsWithoutAllegroId();

    void saveAllegroId(DeduplicatedClientsEntity deduplicatedClient, Integer allegroId);

    void deleteDuplicate(T deduplicatedClientCheck);

    boolean isIndividualClientExist(AllegroClientEntity client);

    void updateOrAddIndividualClient(AllegroClientEntity client);

    DeduplicatedClientsEntity findIndividualParent(AllegroClientEntity client);
}
