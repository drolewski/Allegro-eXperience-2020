package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Component
public class DeduplicatedClientsDAO implements DAO<DeduplicatedClientsEntity> {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void saveDeduplicated(DeduplicatedClientsEntity deduplicatedClientsEntity) {
    }

    @Override
    public List<DeduplicatedClientsEntity> getClientsByNameSurname(String nameSurname) {
        return null;
    }

    @Override
    public List<DeduplicatedClientsEntity> getClientsByNIP(String nip) {
        return (List<DeduplicatedClientsEntity>) entityManager.createQuery(
                "FROM DeduplicatedClientsEntity WHERE nip LIKE :nipVar")
                .setParameter("nipVar", nip)
                .getResultList();
    }

    @Override
    public DeduplicatedClientsEntity getClientByEmail(String email) {
        return null;
    }

    @Override
    public DeduplicatedClientsEntity getClientById(Integer id) {
        return null;
    }

    @Override
    public DeduplicatedClientsEntity getClientByLogin(String login) {
        return null;
    }

    @Override
    public List<DeduplicatedClientsEntity> getDeduplicatedClients() {
        return null;
    }
}
