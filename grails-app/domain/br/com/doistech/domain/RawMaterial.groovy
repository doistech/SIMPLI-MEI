package br.com.doistech.domain

class RawMaterial {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name
    String unitMeasurement
    Double quantity = 0
    Double price = 0

    String materialType

    Company company

    static constraints = {
        price nullable: false
        unitMeasurement nullable: true

        materialType nullable: true
    }
}
