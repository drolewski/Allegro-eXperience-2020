package com.drolewski.allegro.dao;

import com.drolewski.allegro.entity.AllegroClientEntity;
import com.drolewski.allegro.entity.DeduplicatedClientsEntity;
import com.drolewski.allegro.service.DeduplicatedClients;
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
        return entityManager.createQuery("FROM DeduplicatedClientsEntity").getResultList();
    }

    @Override
    public boolean isCompanyClientExist(AllegroClientEntity client) {
        return entityManager.createQuery("FROM DeduplicatedClientsEntity WHERE " +
                "REPLACE(nip, '-', '') LIKE :clientNIP " +
                "AND company_parent = NULL")
                .setParameter("clientNIP", client.getNip())
                .getResultList().size() > 0;
    }

    @Override
    public void updateOrAddClient(AllegroClientEntity client) {
        List<DeduplicatedClientsEntity> queryResultList =
                entityManager.createQuery("FROM DeduplicatedClientsEntity WHERE " +
                "REPLACE(nip, '-', '') LIKE :clientNIP AND id = :clientId ORDER BY id")
                .setParameter("clientNIP", client.getNip())
                .setParameter("clientId", client.getId())
                .getResultList();
        if(queryResultList.size() == 0){
            //add client with this NIP
            for (DeduplicatedClientsEntity clientFromDb : queryResultList){
                if(clientFromDb.getCompanyParent() == null){
                    DeduplicatedClientsEntity newDeduplicatedClient =
                            new DeduplicatedClientsEntity(client.getId(), client.getNameSurname(),
                                    client.getNip(), client.getCompanyName(), client.getEmail(),
                                    client.getPhoneNumber1(), client.getPhoneNumber2(), client.getLogin(),
                                    client.getAddress(),clientFromDb, null);

                    entityManager.getTransaction().begin();
                    entityManager.persist(newDeduplicatedClient);
                    entityManager.getTransaction().commit();
                    break;
                }
            }
        }else{
            //update client with given NIP

            //add historical email
            for(DeduplicatedClientsEntity clientFromDb: queryResultList){
                if(clientFromDb.getId().equals(client.getId())){
                    entityManager.getTransaction().begin();
                    clientFromDb.setNameSurname(client.getNameSurname());
                    clientFromDb.setCompanyName(client.getCompanyName());
                    clientFromDb.setPhoneNumber1(client.getPhoneNumber1());
                    clientFromDb.setPhoneNumber2(client.getPhoneNumber2());
                    clientFromDb.setAddress(client.getAddress());
                    clientFromDb.setLogin(client.getLogin());
                    entityManager.getTransaction().commit();
                    break;
                }
            }
        }
    }

    @Override
    public void addCompanyClient(AllegroClientEntity client) {
        DeduplicatedClientsEntity newDeduplicatedClient =
                new DeduplicatedClientsEntity(
                        client.getId(), client.getNameSurname(), client.getNip(),
                        client.getCompanyName(), client.getEmail(), client.getPhoneNumber1(),
                        client.getPhoneNumber2(), client.getLogin(), client.getAddress(),
                        null, null
                );
        entityManager.getTransaction().begin();
        entityManager.persist(newDeduplicatedClient);
        entityManager.getTransaction().commit();
    }
}
