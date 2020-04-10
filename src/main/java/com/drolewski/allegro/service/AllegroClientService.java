package com.drolewski.allegro.service;

import com.drolewski.allegro.entity.AllegroClientEntity;

import java.util.List;

public interface AllegroClientService {
    List<AllegroClientEntity> getRawAllegroClients();
}
