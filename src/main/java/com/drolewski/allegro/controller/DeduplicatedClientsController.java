package com.drolewski.allegro.controller;

import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
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
    public List<DeduplicatedClientsEntity> getClientsByNip(@PathVariable String nip){
        return this.deduplicatedClients.getDeduplicatedClientsByNIP(nip);
    }
}
