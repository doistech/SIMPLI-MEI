package br.com.doistech.domain

class StatusOrderSales {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String status
    Company company

    static constraints = {
        status nullable:false
    }
}
