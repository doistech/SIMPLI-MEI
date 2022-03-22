package br.com.doistech.domain

class Cliente {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name

    // Contato
    String email
    String phone
    String mobilePhone

    // endereco
    String postalCode
    String address
    String addressComplement
    String addressReference
    Long addressNumber

    // Relação
    Company company

    static constraints = {
        postalCode nullable:true
        address nullable:true
        addressComplement nullable:true
        addressReference nullable:true
        addressNumber nullable:true

        email nullable:true
        phone nullable:true
        mobilePhone nullable:true
    }
}
