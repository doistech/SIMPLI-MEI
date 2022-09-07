package br.com.doistech.domain

class MethodPaymentOrderSales {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    Company company
    OrderSales orderSales
    MethodPayment methodPayment

    Double totalOrder = 0
    Double valuePayment = 0

    static constraints = {
    }
}
