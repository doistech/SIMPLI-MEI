package br.com.doistech.service

import grails.util.Holders

class InjectUtils {

    static def getBean(String beanName) {
        return Holders.grailsApplication.mainContext.getBean(beanName)
    }
}
