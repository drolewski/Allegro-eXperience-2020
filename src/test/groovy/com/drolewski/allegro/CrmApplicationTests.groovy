package com.drolewski.allegro

import spock.lang.Specification


class CrmApplicationTests extends Specification{

    def "First Test"(){
        given:
        def hello = "hello"

        expect:
        hello == "hello"
    }
}
