package br.com.doistech.domain

class MethodPayment {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name
    String description

    Double discountMethodPayment = 0

    Company company

    static constraints = {
        name nullable: false
        description nullable: false
    }
}
