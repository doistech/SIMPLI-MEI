package br.com.doistech.domain

class PackageCompany {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    PackageComerce packageComerce
    Company company

    Date fromDate
    Date toDate

    Boolean packageActive = false
    String type


    static constraints = {
        type nullable: true
    }
}
