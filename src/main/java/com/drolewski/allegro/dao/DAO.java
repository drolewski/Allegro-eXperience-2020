package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;

import java.sql.SQLDataException;
import java.util.List;

public interface DAO<T> {
    List<T> getClientsByNameSurname(String nameSurname);

    List<T> getClientsByNIP(String nip);

    List<T> getClientsByLogin(String login);

    List<T> getListOfDeduplicatedClients();

    boolean isCompanyClientExist(AllegroClientEntity client);

    boolean isIndividualClientExist(AllegroClientEntity client);

    void updateOrAddCompanyClient(AllegroClientEntity client);

    void updateOrAddIndividualClient(AllegroClientEntity client);

    void addNewCRMClient(AllegroClientEntity client) throws SQLDataException;

    List<T> getAccountsWithNULLAllegroId();

    void updateAllegroId(T deduplicatedClient, Integer allegroId);

    void deleteDuplicatedRecords(T deduplicatedClientCheck);
}
