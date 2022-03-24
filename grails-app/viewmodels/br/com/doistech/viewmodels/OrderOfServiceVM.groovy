package br.com.doistech.viewmodels

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.MethodPaymentOrderSales
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.OrderSalesProduct
import br.com.doistech.service.ClientService
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.OrderSalesProductService
import br.com.doistech.service.OrderSalesService
import br.com.doistech.utils.Utils
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.QueryParam

class OrderOfServiceVM {

    OrderSalesProductService orderSalesProductService
    OrderSalesService orderSalesService
    ClientService clientService

    OrderSales orderSales = new OrderSales()
    Cliente cliente = new Cliente()

    List<OrderSales> orderSalesList = []

    List<OrderSalesProduct> orderSalesProductList = []

    List<MethodPaymentOrderSales> methodPaymentOrderSalesList = []

    Double remainsPay = 0
    Double valueSignal = 0

    @Init
    public void init(@QueryParam("orderNumber") String orderNumber) {
        orderSalesProductService = InjectUtils.getBean('orderSalesProductService')
        orderSalesService = InjectUtils.getBean('orderSalesService')
        clientService = InjectUtils.getBean('clientService')

        orderSales = orderSalesService.getOrderSales(orderNumber.toString().toLong())
        cliente = clientService.getClienteById(orderSales.cliente.id)

//        OrderSalesProduct orderSalesProduct

        orderSalesProductList = orderSalesProductService.getOrderSalesProductList(orderSales)
        methodPaymentOrderSalesList = orderSalesProductService.getMethodPaymentOrderSalesList(orderSales)

        remainsPay = calculationRemainsPay()

        valueSignal = orderSales.totalOrder - remainsPay

        println 'ordem de serviço '
    }

    Double calculationRemainsPay(){
        Double sumValuePayment = 0

        methodPaymentOrderSalesList.each {
            sumValuePayment += it.valuePayment
        }

        Double result = orderSales.totalOrder - sumValuePayment
        // Utils.truncar().toString().toDouble()
        return result < 0 ? 0.0 : result
    }
}
