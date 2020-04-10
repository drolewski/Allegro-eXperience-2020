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
    public void saveDeduplicatedClients(List<DeduplicatedClientsEntity> deduplicatedClients) {
        List<AllegroClientEntity> allegroClients = this.allegroClientService.getRawAllegroClients();
        DeduplicatedClientsEntity deduplicatedClientsEntity;
        for(AllegroClientEntity client: allegroClients){
            if(client.getNip() == null){
                deduplicatedClientsEntity = new DeduplicatedClientsEntity();
                deduplicatedClientsEntity.setId(client.getId());
                deduplicatedClientsEntity.setNameSurname(client.getNameSurname());
                deduplicatedClientsEntity.setNip(client.getNip());
                deduplicatedClientsEntity.setCompanyName(client.getCompanyName());
                deduplicatedClientsEntity.setEmail(client.getEmail());
                deduplicatedClientsEntity.setPhoneNumber1(client.getPhoneNumber1());
                deduplicatedClientsEntity.setPhoneNumber2(client.getPhoneNumber2());
                deduplicatedClientsEntity.setLogin(client.getLogin());
                deduplicatedClientsEntity.setAddress(client.getAddress());
                deduplicatedClientDAO.saveDeduplicated(deduplicatedClientsEntity);
            }else{

            }
        }
    }

    @Override
    public List<DeduplicatedClientsEntity> getDeduplicatedClientsByNIP(String nip) {
        return this.deduplicatedClientDAO.getClientsByNIP(nip);
    }
}
