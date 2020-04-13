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

    @Override
    public List<DeduplicatedClientEntity> getAllDeduplicatedClients() {
        return null;
    }

    @Override
    public List<DeduplicatedClientEntity> getDeduplicatedClientsByNIP(String nip) {
        return this.deduplicatedClientDAO.getClientsByNIP(nip);
    }

    @Override
    public List<DeduplicatedClientEntity> getDeduplicatedClientsByNameSurname(String name) {
        return this.deduplicatedClientDAO.getClientsByNameSurname(name);
    }

    @Override
    public List<DeduplicatedClientEntity> importClients() {
        this.updateAllegroId();
        this.deduplicateTableEntities();
        List<AllegroClientEntity> allegroClients = this.allegroClientService.getRawAllegroClients();
        for (AllegroClientEntity client : allegroClients) {
            if (client.getNip() != null) {
                logger.info("Company client: " + client.getId());
                this.deduplicateCompanyClient(client);
            }else {
                logger.info("Individual client: " + client.toString());
                this.deduplicatedIndividualClient(client);
            }
        }
        return this.deduplicatedClientDAO.getListOfDeduplicatedClients();
    }

    private void deduplicatedIndividualClient(AllegroClientEntity client) {
        if(this.deduplicatedClientDAO.isIndividualClientExist(client)){
            logger.info("Exist parent client");
            this.deduplicatedClientDAO.updateOrAddIndividualClient(client);
        }else{
            try {
                this.deduplicatedClientDAO.addNewCRMClient(client);
            }catch (SQLDataException e){
                logger.info(e.getMessage());
            }
        }
    }

    private void deduplicateCompanyClient(AllegroClientEntity client) {
        if (this.deduplicatedClientDAO.isCompanyClientExist(client)) {
            logger.info("Exist parent client");
            this.deduplicatedClientDAO.updateOrAddCompanyClient(client);
        } else {
            try {
                this.deduplicatedClientDAO.addNewCRMClient(client);
            }catch (SQLDataException e){
                logger.info(e.getMessage());
            }
        }
    }

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
