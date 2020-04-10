package com.drolewski.allegro.service;

import com.drolewski.allegro.dao.DAO;
import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeduplicatedClientsImpl implements DeduplicatedClients{

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
            if(client.getNip() == null){
                //company clients
                this.deduplicateCompanyClient(client);
            }else{
                //individual
            }
        }
        return this.deduplicatedClientDAO.getDeduplicatedClients();
    }

    @Override
    public void deduplicateCompanyClient(AllegroClientEntity client){
       if(this.deduplicatedClientDAO.isCompanyClientExist(client)){ //istnieje klient z takim nipem
           this.deduplicatedClientDAO.updateOrAddClient(client);
       }else{
           this.deduplicatedClientDAO.addCompanyClient(client);
       }
    }
}
