package br.com.doistech.domain

class OrderSales {

    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    Company company
    Cliente cliente

    Date orderDate = new Date()
    Date deliveryDate = new Date()

    String orderType
    String orderStatus
    String paymentStatus
    String methodPaymentOrderSales

    Double discount = 0
    Double addition = 0
    Double totalOrder = 0
    Double subtotalOrder = 0
    Double thing = 0

    String jsonHeader
    String jsonFooter

    String orderDescription

    String deleteBy
    Date dateDelete
    Boolean isDelete = false

    static constraints = {

        deleteBy nullable: true
        dateDelete nullable: true
        isDelete nullable: true

        cliente nullable: true
        company nullable: false

        methodPaymentOrderSales nullable: true

        jsonHeader type: 'text', nullable: true
        jsonFooter type: 'text', nullable: true

        orderDescription type: 'text', nullable: true
    }
}
