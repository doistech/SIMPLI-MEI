package br.com.doistech.service

import br.com.doistech.domain.MethodPayment
import br.com.doistech.domain.MethodPaymentOrderSales
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.OrderSalesProduct
import grails.transaction.Transactional
import org.hibernate.FetchMode

@Transactional
class OrderSalesProductService {

    List<OrderSalesProduct> getOrderSalesProductList(OrderSales orderSales){
        List<OrderSalesProduct> orderSalesProductList = OrderSalesProduct.createCriteria().list {
            eq('order',orderSales)
            fetchMode("orderSales", FetchMode.JOIN)
            fetchMode("product", FetchMode.JOIN)
            fetchMode("order", FetchMode.JOIN)
            fetchMode("company", FetchMode.JOIN)
        }
        return orderSalesProductList
    }

    List<MethodPaymentOrderSales> getMethodPaymentOrderSalesList(OrderSales orderSales){
        List<MethodPaymentOrderSales> methodPaymentOrderSalesList = MethodPaymentOrderSales.createCriteria().list {
            eq('orderSales',orderSales)
            fetchMode("orderSales", FetchMode.JOIN)
            fetchMode("methodPayment", FetchMode.JOIN)
        }

        return methodPaymentOrderSalesList
    }
}
