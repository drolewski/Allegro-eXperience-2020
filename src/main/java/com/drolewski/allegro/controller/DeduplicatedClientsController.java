package com.drolewski.allegro.controller;

import com.drolewski.allegro.entity.DeduplicatedClientEntity;
import com.drolewski.allegro.service.DeduplicatedClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allegro")
public class DeduplicatedClientsController {

    private final DeduplicatedClients deduplicatedClients;

    @Autowired
    public DeduplicatedClientsController(DeduplicatedClients deduplicatedClients) {
        this.deduplicatedClients = deduplicatedClients;
    }

    @GetMapping("/{nip}")
    public List<DeduplicatedClientEntity> getClientsByNip(@PathVariable String nip){
        return this.deduplicatedClients.getDeduplicatedClientsByNIP(nip);
    }

    @GetMapping("/{name}")
    public List<DeduplicatedClientEntity> getClientsByNameSurname(@PathVariable String name){
        return this.deduplicatedClients.getDeduplicatedClientsByNameSurname(name);
    }

    @GetMapping("")
    public List<DeduplicatedClientEntity> getAllList(){
        return this.deduplicatedClients.getAllDeduplicatedClients();
    }

    @GetMapping("/import")
    public List<DeduplicatedClientEntity> importAndDeduplicateClients(){
        return this.deduplicatedClients.importClients();
    }
}
