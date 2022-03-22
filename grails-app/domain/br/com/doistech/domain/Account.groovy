package br.com.doistech.domain

class Account {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    User user
    Company company

    String name
    String office

    Date birthDate

    static constraints = {
        birthDate nullable:true
    }
}
