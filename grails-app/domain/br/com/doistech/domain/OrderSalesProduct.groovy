package br.com.doistech.domain

class OrderSalesProduct {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    OrderSales order
    Product product
    Company company

    Double priceItem = 0
    Double discount = 0
    Double addition = 0
    Double totalPriceItem = 0
    Double quantityItem = 0

    String itemDetailValue

    static constraints = {
        itemDetailValue type: 'text', nullable: true
    }
}
