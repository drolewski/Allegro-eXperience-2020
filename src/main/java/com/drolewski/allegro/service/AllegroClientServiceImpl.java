package com.drolewski.allegro.service;

import com.drolewski.allegro.dao.AllegroClientRepository;
import com.drolewski.allegro.entity.AllegroClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllegroClientServiceImpl implements AllegroClientService {

    private final AllegroClientRepository allegroClientRepository;

    @Autowired
    public AllegroClientServiceImpl(AllegroClientRepository allegroClientRepository) {
        this.allegroClientRepository = allegroClientRepository;
    }

    @Override
    public List<AllegroClientEntity> getRawAllegroClients() {
        return this.allegroClientRepository.findAll();
    }
}
