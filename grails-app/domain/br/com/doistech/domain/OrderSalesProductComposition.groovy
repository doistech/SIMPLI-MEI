package br.com.doistech.domain

class OrderSalesProductComposition {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    OrderSales orderSales
    Product product

    String name
    Double itemValue = 0

    static constraints = {
    }
}
