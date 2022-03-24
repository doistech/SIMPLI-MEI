package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.MethodPayment
import br.com.doistech.domain.MethodPaymentOrderSales
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.OrderSalesProduct
import br.com.doistech.domain.ProductRawMaterial
import br.com.doistech.domain.OrderSalesProductComposition
import br.com.doistech.domain.StatusOrderSales
import br.com.doistech.domain.User
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional
import org.hibernate.FetchMode
import org.hibernate.annotations.Fetch

@Transactional
class OrderSalesService {

    ErrorUi errorUi
    MethodPaymentService methodPaymentService
    JsonManipulationService jsonManipulationService
    ProductRawMaterialService productRawMaterialService

    OrderSales getOrderSales(Long orderSalesId){
        return OrderSales.createCriteria().get {
            eq('id',orderSalesId)
            fetchMode("cliente", FetchMode.JOIN)
            fetchMode("company", FetchMode.JOIN)
        }
    }


    List<OrderSales> getOpenOrderSalesList(Date fromDate, Date toDate, List<String> orderStatusList = [], Company company){
        return OrderSales.createCriteria().list {
            if(orderStatusList.size()>0) 'in'('orderStatus', orderStatusList)
            ge('orderDate', fromDate)
            le('orderDate', toDate)
            ne('paymentStatus', 'Pago')
            eq('company', company)
            fetchMode("cliente", FetchMode.JOIN)
            fetchMode("company", FetchMode.JOIN)
        }
    }

    List<OrderSales> getClosedOrderSalesList(Date fromDate, Date toDate, List<String> orderStatusList = [], Company company){
        return OrderSales.createCriteria().list {
            if(orderStatusList.size()>0) 'in'('orderStatus', orderStatusList)
            ge('orderDate', fromDate)
            le('orderDate', toDate)
            eq('paymentStatus', 'Pago')
            eq('company', company)
            fetchMode("cliente", FetchMode.JOIN)
            fetchMode("company", FetchMode.JOIN)
        }
    }

    Map validationOrderSales(OrderSales orderSales){
        Map validationMap = [hasError:false, messageError: '']

        if(!orderSales.cliente){
            validationMap.hasError = true
            validationMap.messageError = 'Por favor, selecione o cliente.'
           return validationMap
        } else {
            return validationMap
        }
    }

    Map validationOrderSalesProductList(List<OrderSalesProduct> orderSalesProductList){
        Map validationMap = [hasError:false, messageError: '']

        if(orderSalesProductList.size() > 0){
            validationMap.hasError = true
            validationMap.messageError = 'Por favor, selecione um pedido.'
            return validationMap
        } else {
            return validationMap
        }
    }

    Map validationOrderSalesMethodPayment(List<Map> methodPaymentOrderSalesList){

        Map validationMap = [hasError:false, messageError: '']

        Double totalMethodPaymentOrderSales = methodPaymentService.sumMethodPaymentOrderSalesList(methodPaymentOrderSalesList)

        if (totalMethodPaymentOrderSales == 0){
            validationMap.hasError = true
            validationMap.messageError = 'Por favor, informe a forma de pagamento.'
            return validationMap
        } else {
            return validationMap
        }
    }

    OrderSales saveOrderSales(OrderSales orderSales, User user, Company company, Cliente cliente, String orderStatus = null, String paymentStatus = null){
        orderSales.createdBy = user.username
        orderSales.updatedBy = user.username
        orderSales.dateCreated = new Date()
        orderSales.dateUpdated = new Date()

        orderSales.company = company
        orderSales.cliente = cliente

        orderSales.orderType = 'Order Sales'

//        if(!orderStatus){
//            orderSales.orderStatus = 'Pendente'
//        } else {
//            orderSales.orderStatus = orderStatus
//        }

//        if(!paymentStatus){
//            orderSales.paymentStatus = 'Pendente de Pagamento'
//        } else {
//            orderSales.paymentStatus = paymentStatus
//        }

        // orderSales = jsonManipulationService.JSONManipulationHeaderAndFooterOrderSales(orderSales)

        try{
            OrderSales.withTransaction {
                orderSales.save(flush:true)
            }
            return orderSales
        }catch(Exception e){
            println "Error saver order sales"
            e.printStackTrace()
            return null
        }
    }

    void saveOrderSalesProduct(OrderSalesProduct orderSalesProduct, OrderSales orderSales, User user, Company company){
        orderSalesProduct.createdBy = user.username
        orderSalesProduct.updatedBy = user.username

        orderSalesProduct.dateCreated = new Date()
        orderSalesProduct.dateUpdated = new Date()

        orderSalesProduct.company = company
        orderSalesProduct.order = orderSales

        List<ProductRawMaterial> productRawMaterialList = productRawMaterialService.getProductRawMaterialCompanyAndProduct(company, orderSalesProduct.product)

        productRawMaterialList.each { prm ->
            OrderSalesProductComposition orderSalesProductComposition

            OrderSalesProductComposition.withTransaction {
                orderSalesProductComposition = new OrderSalesProductComposition()
                orderSalesProductComposition.orderSales = orderSales
                orderSalesProductComposition.product = orderSalesProduct.product
                orderSalesProductComposition.name = prm.rawMaterial.name
                orderSalesProductComposition.itemValue = prm.price
                orderSalesProductComposition.save(flush:true)
            }
        }

        // orderSalesProduct.itemDetailValue = jsonManipulationService.JSONManipulationItemProductOrderSales(orderSalesProduct, productRawMaterialList)

        try{
            orderSalesProduct.save(flush:true)
            println 'Save orderSalesProduct'
        }catch(Exception e){
            println "Error saver order sales product"
            e.printStackTrace()
        }

    }

//
//    Boolean saveMethodPaymentOrderSales(MethodPaymentOrderSales methodPaymentOrderSales){
//
//    }

    void saveMethodPaymentOrderSales(MethodPaymentOrderSales methodPaymentOrderSales, OrderSales orderSales){
        try{
            methodPaymentOrderSales.orderSales = orderSales
            methodPaymentOrderSales.save(flush:true)
            println 'Save'
        } catch(Exception e){
            e.printStackTrace()
        }
    }

    List<MethodPaymentOrderSales> getMethodPaymentOrderSales(OrderSales orderSales){
        return MethodPaymentOrderSales.createCriteria().list {
            eq('orderSales', orderSales)
            fetchMode("company", FetchMode.JOIN)
            fetchMode("orderSales", FetchMode.JOIN)
            fetchMode("methodPayment", FetchMode.JOIN)
        }
    }

    OrderSales finishOrderSales(OrderSales orderSales, User user, Company company, Cliente cliente, List<OrderSalesProduct> orderSalesProductList, List<Map> methodPaymentOrderSalesList = []){
        Boolean hasError = false

//        'Pendente', orderSales.orderStatus
        orderSales = saveOrderSales(orderSales,user,company,cliente)

        if(orderSales){
            orderSalesProductList.each { OrderSalesProduct osp ->
                saveOrderSalesProduct(osp, orderSales, user, company)
            }

            if(methodPaymentOrderSalesList.size() > 0){
                methodPaymentOrderSalesList.each { mpos ->
                    if(mpos.methodPaymentOrderSales.valuePayment > 0){
                        saveMethodPaymentOrderSales(mpos.methodPaymentOrderSales, orderSales)
                    }
                }
            }

        }

        return orderSales
    }

    List<StatusOrderSales> getStatusOrderSalesList(Company company){
        return StatusOrderSales.createCriteria().list {
            eq('company', company)
        }
    }

    Map updatedStatusOrderSales(OrderSales orderSales, List<Map> methodPaymentOrderSalesList = []){
        try{
            orderSales.save(flush:true)

            if(methodPaymentOrderSalesList.size() > 0){
                methodPaymentOrderSalesList.each { mpos ->
                    if(mpos.methodPaymentOrderSales.valuePayment > 0){
                        saveMethodPaymentOrderSales(mpos.methodPaymentOrderSales, orderSales)
                    }
                }
            }
            return ['status':0, 'orderSales':orderSales]
        } catch(Exception e){
            e.printStackTrace()

            return ['status':2, 'orderSales': null]
        }
    }
}
