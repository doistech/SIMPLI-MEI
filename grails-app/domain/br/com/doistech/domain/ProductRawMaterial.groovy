package br.com.doistech.domain

import br.com.doistech.utils.Utils

class ProductRawMaterial {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    Boolean isExclud = false
    String excludBy
    Date dateExclud

    Product product
    RawMaterial rawMaterial

    Double quantity
    Double price

    Company company

    static constraints = {
        excludBy nullable: true
        dateExclud nullable: true
    }

    void calculationPrice(RawMaterial rm){

//        quantity        -  price
//        quantityInsumo  -  Y
//        quantity * Y = price * quantityInsumo
//        Y =  (price * quantityInsumo) / quantity

        this.price = Utils.truncar((rm.price * this.quantity) / rm.quantity)
    }
}
