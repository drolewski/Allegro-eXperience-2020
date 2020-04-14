package com.drolewski.allegro.service

import com.drolewski.allegro.dao.AllegroClientRepository
import com.drolewski.allegro.entity.AllegroClientEntity
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class AllegroClientServiceSpec extends Specification {

    def allegroClientRepository = Mock(AllegroClientRepository)
    def allegroClientService = new AllegroClientServiceImpl(allegroClientRepository)

    def "Retrieve all allegro clients test"() {
        given:
        List<AllegroClientEntity> allegroClients = new ArrayList<>();
        allegroClients << new AllegroClientEntity(
                1, 'test1', 'test1', 'test1', 'test1', 'test1', 'test1', 'test1', 'test1')
        allegroClients << new AllegroClientEntity(
                2, 'test2', 'test2', 'test2', 'test2', 'test2', 'test2', 'test2', 'test2')
        when:
        allegroClientRepository.findAll() >> allegroClients
        then:
        allegroClientService.getRawAllegroClients() != null
        allegroClientService.getRawAllegroClients() == allegroClientRepository.findAll()
        allegroClientService.getRawAllegroClients().size() == 2
    }
}
