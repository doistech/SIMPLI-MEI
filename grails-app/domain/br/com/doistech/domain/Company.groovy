package br.com.doistech.domain

class Company {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name
    String nameFantasy
    String taxid
    String email

    String phone
    String mobilePhone

    String urlImageLogo
    String contactString01
    String contactString02

    static constraints = {
        name nullable: true
        nameFantasy nullable: true
        phone nullable: true
        mobilePhone nullable: true
        urlImageLogo nullable: true
        contactString01 nullable: true
        contactString02 nullable: true
    }
}
