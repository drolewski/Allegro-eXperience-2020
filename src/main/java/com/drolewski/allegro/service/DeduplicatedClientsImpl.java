package com.drolewski.allegro.service;

import com.drolewski.allegro.dao.DAO;
import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;

@Service
public class DeduplicatedClientsImpl implements DeduplicatedClients {
    final Logger logger = LoggerFactory.getLogger(DeduplicatedClientsImpl.class);

    private final AllegroClientService allegroClientService;
    private final DAO<DeduplicatedClientEntity> deduplicatedClientDAO;

    @Autowired
    public DeduplicatedClientsImpl(AllegroClientService allegroClientService, DAO<DeduplicatedClientEntity> deduplicatedClientDAO) {
        this.allegroClientService = allegroClientService;
        this.deduplicatedClientDAO = deduplicatedClientDAO;
    }

    /**
     * @return      Full list of DeduplicatedClients without any conditional
     * */
    @Override
    public List<DeduplicatedClientEntity> getAllDeduplicatedClients() {
        return this.deduplicatedClientDAO.getListOfDeduplicatedClients();
    }

    /**
     * @param nip   Number of tax identification
     * @return      List of DeduplicatedClients with condition on nip number
     * */
    @Override
    public List<DeduplicatedClientEntity> getDeduplicatedClientsByNIP(String nip) {
        return this.deduplicatedClientDAO.getClientsByNIP(nip);
    }

    /**
     * @param name  Name and surname of client
     * @return      List of DeduplicatedClients with condition on name_surname column
     * */
    @Override
    public List<DeduplicatedClientEntity> getDeduplicatedClientsByNameSurname(String name) {
        return this.deduplicatedClientDAO.getClientsByNameSurname(name);
    }

    /**
     * Update AllegroId with proper values from imported data
     * Delete duplicates in our database table
     * Update Data in Database with imported changes
     *
     * @return      Full list of DeduplicatedClients
     * */
    @Override
    public List<DeduplicatedClientEntity> importClients() {
        this.updateAllegroId();
        this.deduplicateTableEntities();
        List<AllegroClientEntity> allegroClients = this.allegroClientService.getRawAllegroClients();
        for (AllegroClientEntity client : allegroClients) {
            if (client.getNip() != null) {
                logger.info("Company client: " + client.getId());
                this.deduplicateCompanyClient(client);
            } else {
                logger.info("Individual client: " + client.toString());
                this.deduplicatedIndividualClient(client);
            }
            this.updateHistoricEmails(client);
        }
        this.deduplicateTableEntities();
        return this.getAllDeduplicatedClients();
    }

    /**
     * Update value of email column if email in CRM is duplicated
     * @param client AllegroClientEntity
     * */
    private void updateHistoricEmails(AllegroClientEntity client) {
        this.deduplicatedClientDAO.updateHistoricEmails(client);
    }

    /**
     * Individual Client
     * Check data to be add as new object or update object which exists in our Database
     *
     * @param client Imported data object
     * */
    private void deduplicatedIndividualClient(AllegroClientEntity client) {
        if (this.deduplicatedClientDAO.isIndividualClientExist(client)) {
            logger.info("Exist parent client");
            this.deduplicatedClientDAO.updateOrAddIndividualClient(client);
        } else {
            try {
                this.deduplicatedClientDAO.addNewCRMClient(client);
            } catch (SQLDataException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Company Client
     * Check data to be add as new object or update object which exists in our Database
     *
     * @param client Imported data object
     * */
    private void deduplicateCompanyClient(AllegroClientEntity client) {
        if (this.deduplicatedClientDAO.isCompanyClientExist(client)) {
            logger.info("Exist parent client");
            this.deduplicatedClientDAO.updateOrAddCompanyClient(client);
        } else {
            try {
                this.deduplicatedClientDAO.addNewCRMClient(client);
            } catch (SQLDataException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Update AllegroId column with null value
     * */
    private void updateAllegroId() {
        List<DeduplicatedClientEntity> deduplicatedClientsEntities =
                this.deduplicatedClientDAO.getAccountsWithNULLAllegroId();
        List<AllegroClientEntity> allegroClients =
                this.allegroClientService.getRawAllegroClients();

        for (DeduplicatedClientEntity deduplicatedClient : deduplicatedClientsEntities) {
            for (AllegroClientEntity allegroClient : allegroClients) {
                if (deduplicatedClient.getLogin().equals(allegroClient.getLogin())) {
                    this.deduplicatedClientDAO.updateAllegroId(deduplicatedClient, allegroClient.getId());
                }
            }
        }
    }

    /**
     * Delete duplicates from table
     * */
    private void deduplicateTableEntities() {
        List<DeduplicatedClientEntity> deduplicatedClientsEntities =
                this.deduplicatedClientDAO.getListOfDeduplicatedClients();
        List<DeduplicatedClientEntity> listToCheck;
        for (DeduplicatedClientEntity deduplicatedClient : deduplicatedClientsEntities) {
            listToCheck = this.deduplicatedClientDAO.getClientsByLogin(deduplicatedClient.getLogin());
            if (listToCheck.size() > 1) {
                for (DeduplicatedClientEntity deduplicatedClientCheck : listToCheck.subList(1, listToCheck.size())) {
                    this.deduplicatedClientDAO.deleteDuplicatedRecords(deduplicatedClientCheck);
                }
            }
        }
    }
}
