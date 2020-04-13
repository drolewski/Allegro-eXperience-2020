package com.drolewski.allegro.service


import com.drolewski.allegro.dao.DAO
import com.drolewski.allegro.entity.AllegroClientEntity
import com.drolewski.allegro.entity.DeduplicatedClientEntity
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class DeduplicatedClientsService extends Specification{

    def allegroClientService = Mock(AllegroClientService)
    def deduplicatedClientRepository = Mock(DAO)
    def deduplicatedClientService = new DeduplicatedClientsImpl(allegroClientService, deduplicatedClientRepository)
    @Shared
    def listOfDeduplicatedClients = new ArrayList<>()

    def setupSpec(){
        listOfDeduplicatedClients << new DeduplicatedClientEntity(
                1, 'test1', 'test1', 'test1', 'test1', 'test1',
                'test1', 'test1', 'test1', null, null)
        listOfDeduplicatedClients << new DeduplicatedClientEntity(
                2, 'test2', 'test2', 'test2', 'test2', 'test2',
                'test2', 'test2', 'test2', null, null)
    }

    def "Retrieve list of Deduplicated clients"(){
        when:
        deduplicatedClientRepository.getListOfDeduplicatedClients() >> listOfDeduplicatedClients
        then:
        deduplicatedClientService.getAllDeduplicatedClients() == listOfDeduplicatedClients
    }

    def "Retrieve list of Deduplicated clients by nip"(){
        given:
        listOfDeduplicatedClients.remove(1)
        when:
        deduplicatedClientRepository.getClientsByNIP('test1') >> listOfDeduplicatedClients
        then:
        deduplicatedClientService.getDeduplicatedClientsByNIP('test1') == listOfDeduplicatedClients
    }

    def "Retrieve list of Deduplicated clients by name and surname"(){
        given:
        listOfDeduplicatedClients.remove(1)
        when:
        deduplicatedClientRepository.getClientsByNameSurname('test1') >> listOfDeduplicatedClients
        then:
        deduplicatedClientService.getDeduplicatedClientsByNameSurname('test1') == listOfDeduplicatedClients
    }
}
