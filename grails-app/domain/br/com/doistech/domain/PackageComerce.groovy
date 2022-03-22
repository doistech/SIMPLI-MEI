package br.com.doistech.domain

class PackageComerce {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name

    String description

    String label

    Double price = 0.0

    Double priceTotal = 0.0

    Long durationMonths

    Long durationDay

    Long countUser = 0


    static constraints = {
        durationMonths nullable: true
        durationDay nullable: true
        countUser nullable: true
        label nullable: true
    }
}
