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

    private final EntityManager entityManager;

    @Autowired
    public DeduplicatedClientsDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<DeduplicatedClientEntity> getClientsByNameSurname(String nameSurname) {
        return entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                "UPPER(name_surname) LIKE :clientName")
                .setParameter("clientName", nameSurname.toUpperCase())
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
    void updateClient(DeduplicatedClientEntity client, AllegroClientEntity allegroClient) {
        client.setNameSurname(allegroClient.getNameSurname());
        client.setEmail(allegroClient.getEmail());
        client.setCompanyName(allegroClient.getCompanyName());
        client.setPhoneNumber1(allegroClient.getPhoneNumber1());
        client.setPhoneNumber2(allegroClient.getPhoneNumber2());
        client.setNip(allegroClient.getNip());
        client.setAddress(allegroClient.getAddress());
        client.setLogin(allegroClient.getLogin());
        client.setAllegroId(allegroClient.getId());
        logger.info("updateClient() - Updated Client: " + client.toString());
    }

    @Transactional
    void changeEmailToHistoric(DeduplicatedClientEntity client, AllegroClientEntity allegroClient) {
        client.setEmail("[HISTORIC]" + allegroClient.getEmail());
        DeduplicatedClientEntity newDeduplicatedClient =
                new DeduplicatedClientEntity(client.getAllegroId(), client.getNameSurname(),
                        client.getNip(), client.getCompanyName(), client.getEmail(),
                        client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                        client.getAddress(), client.getCompanyParent(), client.getIndividualParent());
        entityManager.persist(newDeduplicatedClient);
        logger.info("changeEmailToHistoric() - Update Email: " + client.toString());
    }

    @Transactional
    void addChildCompanyClient(DeduplicatedClientEntity parent, AllegroClientEntity client) {
        DeduplicatedClientEntity individualClientParent = this.findIndividualParent(client);
        DeduplicatedClientEntity newDeduplicatedClient =
                new DeduplicatedClientEntity(client.getId(), client.getNameSurname(),
                        client.getNip(), client.getCompanyName(), client.getEmail(),
                        client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                        client.getAddress(), parent, individualClientParent);
        entityManager.persist(newDeduplicatedClient);
        logger.info("addChildCompanyClient() - New Client with parent: " + newDeduplicatedClient.toString());
    }

    @Transactional
    void addChildIndividualClient(DeduplicatedClientEntity parent, AllegroClientEntity client) {
        DeduplicatedClientEntity companyClient = this.findCompanyParent(client);
        DeduplicatedClientEntity newDeduplicatedClient =
                new DeduplicatedClientEntity(client.getId(), client.getNameSurname(),
                        client.getNip(), client.getCompanyName(), client.getEmail(),
                        client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                        client.getAddress(), companyClient, parent);
        entityManager.persist(newDeduplicatedClient);
        logger.info("addChildIndividualClient() - New Client with parent: " + newDeduplicatedClient.toString());
    }

    @Transactional
    @Override
    public void updateOrAddCompanyClient(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> queryResultList = this.getClientsByNIP(client.getNip());
        DeduplicatedClientEntity parent = null;
        boolean updated = false;
        for (DeduplicatedClientEntity deduplicatedClient : queryResultList) {
            if (client.getId().equals(deduplicatedClient.getAllegroId()) ||
                    (client.getLogin().equals(deduplicatedClient.getLogin()) &&
                            deduplicatedClient.getAllegroId() == null)) {
                this.updateClient(deduplicatedClient, client);
                updated = true;
            }
            if (deduplicatedClient.getCompanyParent() == null) {
                parent = deduplicatedClient;
            }
        }
        if (!updated) {
            this.addChildCompanyClient(parent, client);
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
        int resultSize = entityManager.createQuery("FROM DeduplicatedClientEntity " +
                "WHERE allegro_id = :clientAllegroId")
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

    @Transactional
    @Override
    public void updateHistoricEmails(AllegroClientEntity allegroClient) {
        List<DeduplicatedClientEntity> deduplicatedClients = this.getListOfDeduplicatedClients();
        for(DeduplicatedClientEntity client: deduplicatedClients){
            if (client.getEmail().equals(allegroClient.getEmail()) &&
                    (!allegroClient.getId().equals(client.getAllegroId()) ||
                            !allegroClient.getLogin().equals(client.getLogin()))
                    && !client.getEmail().contains("[HISTORIC]")) {
                this.changeEmailToHistoric(client, allegroClient);
            }
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
        List<DeduplicatedClientEntity> deduplicatedClientsEntities = this.getClientsByNameSurname(client.getNameSurname());
        boolean updated = false;
        DeduplicatedClientEntity parent = null;
        for (DeduplicatedClientEntity deduplicatedClient : deduplicatedClientsEntities) {
            if (client.getId().equals(deduplicatedClient.getAllegroId()) ||
                    (client.getLogin().equals(deduplicatedClient.getLogin()) &&
                            deduplicatedClient.getAllegroId() == null)) {

                this.updateClient(deduplicatedClient, client);
                updated = true;
            }
            if (deduplicatedClient.getIndividualParent() == null) {
                parent = deduplicatedClient;
            }
        }
        if (!updated) {
            this.addChildIndividualClient(parent, client);
        }
    }

    @Transactional
    DeduplicatedClientEntity findCompanyParent(AllegroClientEntity client) {
        List<DeduplicatedClientEntity> result = entityManager.createQuery("FROM DeduplicatedClientEntity WHERE " +
                "REPLACE(nip, '-', '') LIKE :clientNIP " +
                "AND company_parent = NULL")
                .setParameter("clientNIP", client.getNip())
                .getResultList();
        logger.info("findCompanyParent() - Individual client parent");
        return result.size() > 0 ? result.get(0) : null;
    }
}
