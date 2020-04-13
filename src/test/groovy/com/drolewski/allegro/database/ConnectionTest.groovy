package com.drolewski.allegro.database

import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Specification

class ConnectionTest extends Specification {

    @Shared
    Sql sql

    def setupSpec() {
        sql = Sql.newInstance('jdbc:mysql://localhost:3306/allegro?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC',
                'root',
                'dominik4')
        sql.execute("""
            INSERT INTO allegro_client_deduplicated(
            name_surname, nip, company_name, email, phone_number1, 
            phone_number2, login, address, company_parent,
            individual_parent, allegro_id) 
            VALUES('Test','test', 'test', 'test@test.test', 'test', 'test', 'test', 'test', NULL , NULL, NULL);
            """)
    }

    def cleanupSpec() {
        sql.execute("""
            DELETE FROM allegro_client_deduplicated WHERE name_surname = 'Test' AND 
            nip = 'test' AND company_name = 'test' AND email = 'test@test.test' AND 
            phone_number1 = 'test' AND phone_number2 = 'test' AND login = 'test'
            """)
    }

    def "database connection test"() {
        when:
        def row = sql.firstRow("""
            SELECT * FROM allegro_client_deduplicated WHERE
            nip = 'test' AND company_name = 'test' AND email = 'test@test.test' AND 
            phone_number1 = 'test' AND phone_number2 = 'test' AND login = 'test'
            """)
        def nameSurname = row.values().getAt(1)

        then:
        nameSurname == 'Test'
        nameSurname != 'Another'
    }
}
