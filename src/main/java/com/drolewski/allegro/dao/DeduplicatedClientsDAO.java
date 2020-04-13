package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.util.List;

@Component
public class DeduplicatedClientsDAO implements DAO<DeduplicatedClientEntity> {

    final Logger logger = LoggerFactory.getLogger(DeduplicatedClientsDAO.class);

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<DeduplicatedClientEntity> getClientsByNameSurname(String nameSurname) {
        return entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                "name_surname LIKE :clientName")
                .setParameter("clientName", nameSurname)
                .getResultList();
    }

    @Override
    public List<DeduplicatedClientEntity> getClientsByNIP(String nip) {
        return (List<DeduplicatedClientEntity>) entityManager.createQuery(
                "FROM DeduplicatedClientEntity WHERE nip LIKE :nipVar")
                .setParameter("nipVar", nip)
                .getResultList();
    }

    @Override
    public List<DeduplicatedClientEntity> getClientsByLogin(String login) {
        return entityManager.createQuery("FROM DeduplicatedClientEntity WHERE login LIKE :clientLogin")
                .setParameter("clientLogin", login)
                .getResultList();
    }

    @Override
    public List<DeduplicatedClientEntity> getListOfDeduplicatedClients() {
        return entityManager.createQuery("FROM DeduplicatedClientEntity").getResultList();
    }

    @Override
    public boolean isCompanyClientExist(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> deduplicatedClientsEntities =
                entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                        "REPLACE(nip, '-', '') LIKE :clientNIP " +
                        "AND company_parent = NULL")
                        .setParameter("clientNIP", client.getNip().replaceAll("-", ""))
                        .getResultList();
        logger.info("isCompanyClientExist() - List Size: " + deduplicatedClientsEntities.size());
        return deduplicatedClientsEntities.size() > 0;
    }

    @Transactional
    @Override
    public void updateOrAddCompanyClient(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> queryResultList =
                entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                        "REPLACE(nip, '-', '') LIKE :clientNIP ORDER BY id")
                        .setParameter("clientNIP", client.getNip().replaceAll("-", ""))
                        .getResultList();
        DeduplicatedClientEntity parent = null;
        boolean updated = false;
        for (DeduplicatedClientEntity clientFromDb : queryResultList) {
            if (client.getId().equals(clientFromDb.getAllegroId())||
                    (client.getLogin().equals(clientFromDb.getLogin()) &&
                            clientFromDb.getAllegroId() == null)) {
                clientFromDb.setNameSurname(client.getNameSurname());
                clientFromDb.setCompanyName(client.getCompanyName());
                clientFromDb.setPhoneNumber1(client.getPhoneNumber1());
                clientFromDb.setPhoneNumber2(client.getPhoneNumber2());
                clientFromDb.setAddress(client.getAddress());
                clientFromDb.setLogin(client.getLogin());

                logger.info("updateOrAddCompanyClient() - Updated Client: " + clientFromDb.toString());
                updated = true;
            }
            if (clientFromDb.getEmail().equals(client.getEmail()) &&
                    (!client.getId().equals(clientFromDb.getAllegroId()) ||
                            !client.getLogin().equals(clientFromDb.getLogin()))) {
                clientFromDb.setEmail("[HISTORIC]" + client.getEmail());
                DeduplicatedClientEntity newDeduplicatedClient =
                        new DeduplicatedClientEntity(clientFromDb.getAllegroId(), clientFromDb.getNameSurname(),
                                clientFromDb.getNip(), clientFromDb.getCompanyName(), clientFromDb.getEmail(),
                                clientFromDb.getPhoneNumber1(), clientFromDb.getPhoneNumber2(), clientFromDb.getLogin(),
                                clientFromDb.getAddress(), clientFromDb.getCompanyParent(), clientFromDb.getIndividualParent());
                entityManager.persist(newDeduplicatedClient);
            }
            if (clientFromDb.getCompanyParent() == null) {
                parent = clientFromDb;
            }
        }
        if (!updated) {
            DeduplicatedClientEntity individualClientParent = this.findIndividualParent(client);
            DeduplicatedClientEntity newDeduplicatedClient =
                    new DeduplicatedClientEntity(client.getId(), client.getNameSurname(),
                            client.getNip(), client.getCompanyName(), client.getEmail(),
                            client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                            client.getAddress(), parent, individualClientParent);
            entityManager.persist(newDeduplicatedClient);
            logger.info("updateOrAddCompanyClient() - New Client with parent: " + newDeduplicatedClient.toString());
        }
    }

    @Transactional
    public DeduplicatedClientEntity findIndividualParent(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> result = entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                "UPPER(name_surname) LIKE :clientName " +
                "AND individual_parent = NULL")
                .setParameter("clientName", "%" + client.getNameSurname().toUpperCase() + "%")
                .getResultList();
        logger.info("findIndividualParent() - Individual client parent");
        return result.size() > 0 ? result.get(0) : null;
    }

    @Transactional
    @Override
    public void addNewCRMClient(AllegroClientEntity client) throws SQLDataException {
        int resultSize = entityManager.createQuery("FROM DeduplicatedClientEntity WHERE allegro_id = :clientAllegroId")
                .setParameter("clientAllegroId", client.getId())
                .getResultList().size();
        if (resultSize == 0) {
            DeduplicatedClientEntity newDeduplicatedClient =
                    new DeduplicatedClientEntity(
                            client.getId(), client.getNameSurname(), client.getNip(),
                            client.getCompanyName(), client.getEmail(), client.getPhoneNumber1(),
                            client.getPhoneNumber2(), client.getLogin(), client.getAddress(),
                            null, null
                    );
            entityManager.persist(newDeduplicatedClient);

            logger.info("addNewCRMClient() - New Parent Client: " + newDeduplicatedClient.toString());
            return;
        }
        throw new SQLDataException("Constraints Error: Allegro Client Id already exists in DB");
    }

    @Override
    public List<DeduplicatedClientEntity> getAccountsWithNULLAllegroId() {
        return entityManager.createQuery("FROM DeduplicatedClientEntity where allegroId = null")
                .getResultList();
    }

    @Transactional
    @Override
    public void updateAllegroId(DeduplicatedClientEntity deduplicatedClient, Integer allegroId) {
        logger.info("updateAllegroId() - Update AllegroId: " + allegroId + " on: " + deduplicatedClient.toString());
        deduplicatedClient.setAllegroId(allegroId);
        entityManager.persist(deduplicatedClient);
    }

    @Transactional
    @Override
    public void deleteDuplicatedRecords(DeduplicatedClientEntity deduplicatedClientCheck) {
        if (entityManager.contains(deduplicatedClientCheck)) {
            logger.info("deleteDuplicatedRecords() - Delete from Database: " + deduplicatedClientCheck.toString());
            entityManager.remove(deduplicatedClientCheck);
        }
    }

    @Override
    public boolean isIndividualClientExist(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> deduplicatedClientsEntities =
                entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                        "UPPER(name_surname) LIKE :clientName " +
                        "AND individual_parent = NULL")
                        .setParameter("clientName", "%" + client.getNameSurname().toUpperCase() + "%")
                        .getResultList();
        logger.info("isIndividualClientExist() - List Size: " + deduplicatedClientsEntities.size());
        return deduplicatedClientsEntities.size() > 0;
    }

    @Transactional
    @Override
    public void updateOrAddIndividualClient(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> deduplicatedClientsEntities =
                entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                        "UPPER(name_surname) LIKE :clientName  ORDER BY id")
                        .setParameter("clientName", "%" + client.getNameSurname().toUpperCase() + "%")
                        .getResultList();
        boolean updated = false;
        DeduplicatedClientEntity parent = null;
        for (DeduplicatedClientEntity deduplicatedClient : deduplicatedClientsEntities) {
            if (client.getId().equals(deduplicatedClient.getAllegroId()) ||
                    (client.getLogin().equals(deduplicatedClient.getLogin()) &&
                            deduplicatedClient.getAllegroId() == null)) {
                deduplicatedClient.setNip(client.getNip());
                deduplicatedClient.setNameSurname(client.getNameSurname());
                deduplicatedClient.setCompanyName(client.getCompanyName());
                deduplicatedClient.setPhoneNumber1(client.getPhoneNumber1());
                deduplicatedClient.setPhoneNumber2(client.getPhoneNumber2());
                deduplicatedClient.setAddress(client.getAddress());
                deduplicatedClient.setLogin(client.getLogin());
                deduplicatedClient.setEmail(client.getEmail());

                logger.info("updateOrAddIndividualClient() - Updated Client: " + deduplicatedClient.toString());
                updated = true;
            }
            if (deduplicatedClient.getEmail().equals(client.getEmail()) &&
                    (!client.getId().equals(deduplicatedClient.getAllegroId()) ||
                            !client.getLogin().equals(deduplicatedClient.getLogin()))) {
                deduplicatedClient.setEmail("[HISTORIC]" + client.getEmail());
                DeduplicatedClientEntity newDeduplicatedClient =
                        new DeduplicatedClientEntity(deduplicatedClient.getAllegroId(), deduplicatedClient.getNameSurname(),
                                deduplicatedClient.getNip(), deduplicatedClient.getCompanyName(), deduplicatedClient.getEmail(),
                                deduplicatedClient.getPhoneNumber1(), deduplicatedClient.getPhoneNumber2(), deduplicatedClient.getLogin(),
                                deduplicatedClient.getAddress(), deduplicatedClient.getCompanyParent(), deduplicatedClient.getIndividualParent());
                entityManager.persist(newDeduplicatedClient);
            }
            if (deduplicatedClient.getIndividualParent() == null) {
                parent = deduplicatedClient;
            }
        }
        if (!updated) {
            DeduplicatedClientEntity companyParent = this.findCompanyParent(client);
            DeduplicatedClientEntity newDeduplicatedClient =
                    new DeduplicatedClientEntity(client.getId(), client.getNameSurname(),
                            client.getNip(), client.getCompanyName(), client.getEmail(),
                            client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                            client.getAddress(), companyParent, parent);
            entityManager.persist(newDeduplicatedClient);
            logger.info("updateOrAddIndividualClient() - New Client with parent: " + newDeduplicatedClient.toString());
        }
    }

    @Transactional
    private DeduplicatedClientEntity findCompanyParent(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> result = entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                "REPLACE(nip, '-', '') LIKE :clientNIP " +
                "AND company_parent = NULL")
                .setParameter("clientNIP", client.getNip())
                .getResultList();
        logger.info("findCompanyParent() - Individual client parent");
        return result.size() > 0 ? result.get(0) : null;
    }
}
