package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientEntity;

import java.sql.SQLDataException;
import java.util.List;

public interface DAO<T> {
    void saveDeduplicated(T deduplicatedClientEntity);
    List<T> getClientsByNameSurname(String nameSurname);
    List<T> getClientsByNIP(String nip);
    T getClientByEmail(String email);
    T getClientById(Integer id);
    List<T> getClientsByLogin(String login);
    List<T> getDeduplicatedClients();

    boolean isCompanyClientExist(AllegroClientEntity client);

    void updateOrAddClient(AllegroClientEntity client);

    void addNewCRMClient(AllegroClientEntity client) throws SQLDataException;

    List<T> getAccountsWithoutAllegroId();

    void saveAllegroId(T deduplicatedClient, Integer allegroId);

    void deleteDuplicate(T deduplicatedClientCheck);

    boolean isIndividualClientExist(AllegroClientEntity client);

    void updateOrAddIndividualClient(AllegroClientEntity client);

    T findIndividualParent(AllegroClientEntity client);
}
