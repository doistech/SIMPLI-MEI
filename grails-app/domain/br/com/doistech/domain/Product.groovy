package br.com.doistech.domain

import br.com.doistech.utils.Utils

class Product {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name
    String description

    String codeEan

    Double productOrderPrice = 0
    Double salesPrice = 0
    Double wholesalePrice = 0

    Double wholesaleQuantity = 0

    Double profit = 0
    Double profitWholesale = 0

    Double percentageOrderPrice = 0
    Double percentageWholesalePrice = 0

    Company company

    Boolean hasPriceMethod = false

    Double fixedCost = 0
    Double otherCosts = 0
    Double packagingCost = 0

    String deleteBy
    Date dateDelete
    Boolean isDelete = false

    static constraints = {
        description nullable: true
        codeEan nullable: true
        deleteBy nullable: true
        dateDelete nullable: true
    }

    void calculationPriceSales(Double _productOrderPrice){
        if(percentageOrderPrice > 0){
            salesPrice = Utils.truncar(_productOrderPrice + fixedCost + otherCosts + packagingCost + (_productOrderPrice * (percentageOrderPrice/100)))
            profit = Utils.truncar((_productOrderPrice + fixedCost + otherCosts + packagingCost) * (percentageOrderPrice/100))
        } else {
            salesPrice = 0
            profit = 0
        }
    }

    void calculationWholesalePrice(Double _productOrderPrice){
        if(percentageWholesalePrice > 0){
            wholesalePrice =  Utils.truncar(_productOrderPrice + fixedCost + otherCosts + packagingCost + (_productOrderPrice * (percentageWholesalePrice/100)))
            profitWholesale =  Utils.truncar((_productOrderPrice + fixedCost + otherCosts + packagingCost) * (percentageWholesalePrice/100))
        } else {
            wholesalePrice = 0
            profitWholesale = 0
        }
    }

    void calculationSalesOrderPrice(List<ProductRawMaterial> productRawMaterialList){
        productOrderPrice = 0
        productRawMaterialList.each { prm ->
            productOrderPrice += Utils.truncar(prm.price)
        }
    }
}
