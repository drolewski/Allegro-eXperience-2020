package com.drolewski.allegro.controller;

import com.drolewski.allegro.dao.AllegroClientRepository;
import com.drolewski.allegro.entity.AllegroClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/allegro")
public class AllegroClientController {

    private final AllegroClientRepository allegroClientRepository;

    @Autowired
    public AllegroClientController(AllegroClientRepository allegroClientRepository) {
        this.allegroClientRepository = allegroClientRepository;
    }

    @GetMapping("")
    public List<AllegroClientEntity> getClients(){
        return this.allegroClientRepository.findAll();
    }
}
