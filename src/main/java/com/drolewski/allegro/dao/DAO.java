package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;

import java.sql.SQLDataException;
import java.util.List;

public interface DAO<T> {
    /**
     * Abstract method in DAO interface to retrieve clients by NameSurname column
     * @param nameSurname String data
     *
     * @return List of Generic object T
     * */
    List<T> getClientsByNameSurname(String nameSurname);

    /**
     * Abstract method in DAO interface to retrieve clients by nip column
     * @param nip String data
     *
     * @return List of Generic object T
     * */
    List<T> getClientsByNIP(String nip);

    /**
     * Abstract method in DAO interface to retrieve clients by login column
     * @param login String data
     *
     * @return List of Generic object T
     * */
    List<T> getClientsByLogin(String login);

    /**
     * Abstract method in DAO interface to retrieve all clients
     *
     * @return List of Generic object T
     * */
    List<T> getListOfDeduplicatedClients();

    /**
     * Abstract method in DAO interface to check existence of allegro company client
     *
     * @return existence status of imported allegro client
     * */
    boolean isCompanyClientExist(AllegroClientEntity client);

    /**
     * Abstract method in DAO interface to check existence of allegro individual client
     *
     * @return existence status of imported allegro client
     * */
    boolean isIndividualClientExist(AllegroClientEntity client);

    /**
     * Abstract method in DAO interface to check update company clients row
     * */
    void updateOrAddCompanyClient(AllegroClientEntity client);

    /**
     * Abstract method in DAO interface to check update individual clients row
     * */
    void updateOrAddIndividualClient(AllegroClientEntity client);

    /**
     * Abstract method in DAO interface to add new CRM client
     * @throws SQLDataException Constraint Exceptions
     * @param client Allegro Client
     * */
    void addNewCRMClient(AllegroClientEntity client) throws SQLDataException;

    /**
     * Abstract method in DAO interface to get all accounts with null allegroId
     * @return List of generic type T
     * */
    List<T> getAccountsWithNULLAllegroId();

    /**
     * Abstract method in DAO interface to update null allegroId in database
     * @param deduplicatedClient parameter with generic T type to describe client
     * @param allegroId allegroId retrieve from allegro client
     * */
    void updateAllegroId(T deduplicatedClient, Integer allegroId);

    /**
     * Abstract method in DAO interface to delete given as parameter object in type T
     * @param deduplicatedClientCheck parameter with generic T type to describe client to delete
     * */
    void deleteDuplicatedRecords(T deduplicatedClientCheck);

    /**
     * Update Historical Emails in CRM
     * @param client AllegroClientEntity
    * */
    void updateHistoricEmails(AllegroClientEntity client);
}
