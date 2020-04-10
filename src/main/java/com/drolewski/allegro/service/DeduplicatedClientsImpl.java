package com.drolewski.allegro.service;

import com.drolewski.allegro.dao.DAO;
import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeduplicatedClientsImpl implements DeduplicatedClients{
    final Logger logger = LoggerFactory.getLogger(DeduplicatedClientsImpl.class);

    private final AllegroClientService allegroClientService;
    private final DAO<DeduplicatedClientsEntity> deduplicatedClientDAO;

   @Autowired
    public DeduplicatedClientsImpl(AllegroClientService allegroClientService, DAO<DeduplicatedClientsEntity> deduplicatedClientDAO) {
        this.allegroClientService = allegroClientService;
        this.deduplicatedClientDAO = deduplicatedClientDAO;
    }

    @Override
    public List<DeduplicatedClientsEntity> getAllDeduplicatedClients() {
        return null;
    }

    @Override
    public List<DeduplicatedClientsEntity> getDeduplicatedClientsByNIP(String nip) {
        return this.deduplicatedClientDAO.getClientsByNIP(nip);
    }

    @Override
    public List<DeduplicatedClientsEntity> importClients() {
        List<AllegroClientEntity> allegroClients = this.allegroClientService.getRawAllegroClients();
        for(AllegroClientEntity client: allegroClients){
            if(client.getNip() != null){
                logger.info("Company client: " + client.getId());
                this.deduplicateCompanyClient(client);
            }else{
                logger.info("Individual client: " + client.getId());
            }
        }
        return this.deduplicatedClientDAO.getDeduplicatedClients();
    }

    @Override
    public void deduplicateCompanyClient(AllegroClientEntity client){
       if(this.deduplicatedClientDAO.isCompanyClientExist(client)){
           logger.info("Exist parent client");
           this.deduplicatedClientDAO.updateOrAddClient(client);
       }else{
           this.deduplicatedClientDAO.addCompanyClient(client);
       }
    }

    @Override
    public void updateAllegroId() { //second step of deduplication
        List<DeduplicatedClientsEntity> deduplicatedClientsEntities =
                this.deduplicatedClientDAO.getAccountsWithoutAllegroId();
        List<AllegroClientEntity> allegroClients =
                this.allegroClientService.getRawAllegroClients();

        for(DeduplicatedClientsEntity deduplicatedClient : deduplicatedClientsEntities){
            for(AllegroClientEntity allegroClient : allegroClients){
                if(deduplicatedClient.getLogin().equals(allegroClient.getLogin())){
                    this.deduplicatedClientDAO.saveAllegroId(deduplicatedClient, allegroClient.getId());
                }
            }
        }
    }

    @Override
    public void deduplicateTableEntities() { //first step of deduplication
        List<DeduplicatedClientsEntity> deduplicatedClientsEntities =
                this.deduplicatedClientDAO.getDeduplicatedClients();
        List<DeduplicatedClientsEntity> listToCheck;
        for(DeduplicatedClientsEntity deduplicatedClient : deduplicatedClientsEntities){
            listToCheck = this.deduplicatedClientDAO.getClientsByLogin(deduplicatedClient.getLogin());
            if(listToCheck.size() > 1) {
                for (DeduplicatedClientsEntity deduplicatedClientCheck : listToCheck.subList(1, listToCheck.size())) {
                    this.deduplicatedClientDAO.deleteDuplicate(deduplicatedClientCheck);
                }
            }
        }
    }
}
