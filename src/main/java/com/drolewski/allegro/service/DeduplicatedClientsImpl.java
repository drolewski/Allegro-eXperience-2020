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
}
