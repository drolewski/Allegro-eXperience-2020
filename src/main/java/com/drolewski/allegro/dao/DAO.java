package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientEntity;

import java.sql.SQLDataException;
import java.util.List;

public interface DAO<T> {
    void saveDeduplicated(DeduplicatedClientEntity deduplicatedClientEntity);
    List<DeduplicatedClientEntity> getClientsByNameSurname(String nameSurname);
    List<DeduplicatedClientEntity> getClientsByNIP(String nip);
    DeduplicatedClientEntity getClientByEmail(String email);
    DeduplicatedClientEntity getClientById(Integer id);
    List<DeduplicatedClientEntity> getClientsByLogin(String login);
    List<DeduplicatedClientEntity> getDeduplicatedClients();

    boolean isCompanyClientExist(AllegroClientEntity client);

    void updateOrAddClient(AllegroClientEntity client);

    void addNewCRMClient(AllegroClientEntity client) throws SQLDataException;

    List<DeduplicatedClientEntity> getAccountsWithoutAllegroId();

    void saveAllegroId(DeduplicatedClientEntity deduplicatedClient, Integer allegroId);

    void deleteDuplicate(T deduplicatedClientCheck);

    boolean isIndividualClientExist(AllegroClientEntity client);

    void updateOrAddIndividualClient(AllegroClientEntity client);

    DeduplicatedClientEntity findIndividualParent(AllegroClientEntity client);
}
