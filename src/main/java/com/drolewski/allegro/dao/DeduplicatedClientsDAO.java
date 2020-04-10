package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class DeduplicatedClientsDAO implements DAO<DeduplicatedClientsEntity> {

    final Logger logger = LoggerFactory.getLogger(DeduplicatedClientsDAO.class);

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
    public List<DeduplicatedClientsEntity> getClientsByLogin(String login) {
        return entityManager.createQuery("FROM DeduplicatedClientsEntity WHERE login LIKE :clientLogin")
                .setParameter("clientLogin", login)
                .getResultList();
    }

    @Override
    public List<DeduplicatedClientsEntity> getDeduplicatedClients() {
        return entityManager.createQuery("FROM DeduplicatedClientsEntity").getResultList();
    }

    @Override
    public boolean isCompanyClientExist(AllegroClientEntity client) {
        List<DeduplicatedClientsEntity> deduplicatedClientsEntities =
                entityManager.createQuery("FROM DeduplicatedClientsEntity WHERE " +
                        "REPLACE(nip, '-', '') LIKE :clientNIP " +
                        "AND company_parent = NULL")
                        .setParameter("clientNIP", client.getNip().replaceAll("-", ""))
                        .getResultList();
        logger.info("List Size: " + deduplicatedClientsEntities.size());
        return deduplicatedClientsEntities.size() > 0;
    }

    @Transactional
    @Override
    public void updateOrAddClient(AllegroClientEntity client) {
        List<DeduplicatedClientsEntity> queryResultList =
                entityManager.createQuery("FROM DeduplicatedClientsEntity WHERE " +
                        "REPLACE(nip, '-', '') LIKE :clientNIP ORDER BY id")
                        .setParameter("clientNIP", client.getNip().replaceAll("-", ""))
                        .getResultList();
        DeduplicatedClientsEntity parent = null;
        boolean updated = false;
        for (DeduplicatedClientsEntity clientFromDb : queryResultList) {
            if (clientFromDb.getAllegroId().equals(client.getId()) && clientFromDb.getLogin().equals(client.getLogin())) {
                clientFromDb.setNameSurname(client.getNameSurname());
                clientFromDb.setCompanyName(client.getCompanyName());
                clientFromDb.setPhoneNumber1(client.getPhoneNumber1());
                clientFromDb.setPhoneNumber2(client.getPhoneNumber2());
                clientFromDb.setAddress(client.getAddress());
                clientFromDb.setLogin(client.getLogin());
                clientFromDb.setAllegroId(client.getId());

                logger.info("Updated Client: " + clientFromDb.toString());
                updated = true;
            }
            if (clientFromDb.getEmail().equals(client.getEmail()) &&
                    !clientFromDb.getLogin().equals(client.getLogin())) {
                clientFromDb.setEmail("[HISTORIC]" + client.getEmail());
                DeduplicatedClientsEntity newDeduplicatedClient =
                        new DeduplicatedClientsEntity(clientFromDb.getAllegroId(), clientFromDb.getNameSurname(),
                                clientFromDb.getNip(), clientFromDb.getCompanyName(), clientFromDb.getEmail(),
                                clientFromDb.getPhoneNumber1(), clientFromDb.getPhoneNumber2(), clientFromDb.getLogin(),
                                clientFromDb.getAddress(), clientFromDb.getCompanyParent(), null);
                entityManager.persist(newDeduplicatedClient);
            }
            if (clientFromDb.getCompanyParent() == null) {
                parent = clientFromDb;
            }
        }
        if (!updated) {
            DeduplicatedClientsEntity newDeduplicatedClient =
                    new DeduplicatedClientsEntity(client.getId(), client.getNameSurname(),
                            client.getNip(), client.getCompanyName(), client.getEmail(),
                            client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                            client.getAddress(), parent, null);
            entityManager.persist(newDeduplicatedClient);
            logger.info("New Client with parent: " + newDeduplicatedClient.toString());
        }
    }

    @Transactional
    @Override
    public void addCompanyClient(AllegroClientEntity client) {
        DeduplicatedClientsEntity newDeduplicatedClient =
                new DeduplicatedClientsEntity(
                        client.getId(), client.getNameSurname(), client.getNip(),
                        client.getCompanyName(), client.getEmail(), client.getPhoneNumber1(),
                        client.getPhoneNumber2(), client.getLogin(), client.getAddress(),
                        null, null
                );
        entityManager.persist(newDeduplicatedClient);

        logger.info("New Client without parent: " + newDeduplicatedClient.toString());
    }

    @Override
    public List<DeduplicatedClientsEntity> getAccountsWithoutAllegroId() {
        return entityManager.createQuery("SELECT login FROM DeduplicatedClientsEntity where allegroId = null")
                .getResultList();
    }

    @Transactional
    @Override
    public void saveAllegroId(DeduplicatedClientsEntity deduplicatedClient, Integer allegroId) {
        deduplicatedClient.setAllegroId(allegroId);
        entityManager.persist(deduplicatedClient);
    }

    @Transactional
    @Override
    public void deleteDuplicate(DeduplicatedClientsEntity deduplicatedClientCheck) {
        if (entityManager.contains(deduplicatedClientCheck)) {
            entityManager.remove(deduplicatedClientCheck);
        }
    }
}
