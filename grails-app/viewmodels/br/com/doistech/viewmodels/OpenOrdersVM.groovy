package br.com.doistech.viewmodels

import br.com.doistech.domain.Company
import br.com.doistech.domain.MethodPaymentOrderSales
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.OrderSalesProduct
import br.com.doistech.domain.Product
import br.com.doistech.domain.StatusOrderSales
import br.com.doistech.domain.User
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.MethodPaymentService
import br.com.doistech.service.OrderSalesProductService
import br.com.doistech.service.OrderSalesService
import br.com.doistech.utils.Utils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class OpenOrdersVM {
    // Services
    OrderSalesService orderSalesService
    OrderSalesProductService orderSalesProductService
    MethodPaymentService methodPaymentService

    Session session

    // Lists
    List<OrderSales> orderSalesList
    List<OrderSalesProduct> orderSalesProductList
    List<MethodPaymentOrderSales> methodPaymentOrderSalesList
    List<StatusOrderSales> statusOrdemSalesList
    List<Map> methodPaymentOrderSalesMapList = []

    // Objects
    User user
    Company company
    OrderSales orderSales
    OrderSalesProduct orderSalesProduct
    StatusOrderSales statusOrdemSalesSelect

    // Variables
    Date orderSalesFromDate = new Date()
    Date orderSalesToDate = new Date()
    Boolean visibleForm = false
    String titleFormOrderSales
    Double remainsPay = 0
    Double totalOrders = 0

    //Maps
    Map searchMonthDate = [:]


    @Init
    public void init() {
        orderSalesProductService = InjectUtils.getBean('orderSalesProductService')
        methodPaymentService = InjectUtils.getBean('methodPaymentService')
        orderSalesService = InjectUtils.getBean('orderSalesService')


        session = Sessions.getCurrent()

        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        defaultFromAndToDateMonth()

        orderSalesList = orderSalesService.getOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate,
                'Pendente')

        methodPaymentOrderSalesMapList = methodPaymentService.getMethodPaymentOrderList(company)

        println 'Testando por aqui'
    }

    @NotifyChange(['*'])
    void defaultFromAndToDateMonth(){
        Calendar c = Calendar.getInstance()

        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        searchMonthDate.orderSalesFromDate = c.getTime()

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        searchMonthDate.orderSalesToDate = c.getTime()

    }

    @NotifyChange(['*'])
    void defaultStatusOrderSalesList(){
        statusOrdemSalesSelect = statusOrdemSalesList.find{ it.status == 'Selecione o Status'}
    }

    @Command
    @NotifyChange(['*'])
    void searchOrderSales(String status){
        List<String> statusOrderSalesList = ['Pendente', 'Em produção', 'Concluído']
        orderSalesList = orderSalesService.getOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate, status)
    }

    @Command
    @NotifyChange(['*'])
    void selectOrderSalesGrid(@BindingParam("orderSalesSelect") OrderSales orderSalesSelect){
        orderSales = orderSalesSelect
        statusOrdemSalesList = orderSalesService.getStatusOrderSalesList(company)
        defaultStatusOrderSalesList()
        titleFormOrderSales = 'Pedido: #' + orderSales.id
        orderSalesProductList = orderSalesProductService.getOrderSalesProductList(orderSales)
        methodPaymentOrderSalesList = orderSalesService.getMethodPaymentOrderSales(orderSales)
        remainsPay = calculationRemainsPay()
        totalOrders = remainsPay
        visibleForm = true
    }

    @Command
    @NotifyChange(['*'])
    void returnListOrderSales() {
        defaultFromAndToDateMonth()
        orderSalesList = orderSalesService.getOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate,
                'Pendente')
        visibleForm = false
    }

    Double calculationRemainsPay(){
        Double sumValuePayment = 0

        methodPaymentOrderSalesList.each {
            sumValuePayment += it.valuePayment
        }

        Double result = Utils.truncar(orderSales.totalOrder - sumValuePayment)
        return result < 0 ? 0.0 : result
    }

    @Command
    @NotifyChange(['*'])
    void paymentControl(@BindingParam("methodPaymentOrderSales") def methodPaymentOrderSales){

        totalOrders = calculationRemainsPay()

        methodPaymentOrderSalesMapList.each { mpos ->
            if(totalOrders > mpos.methodPaymentOrderSales.valuePayment){
                totalOrders = totalOrders - mpos.methodPaymentOrderSales.valuePayment
            }else{
                mpos.methodPaymentOrderSales.valuePayment = totalOrders
                totalOrders = Utils.truncar(totalOrders - mpos.methodPaymentOrderSales.valuePayment)
            }
        }

        println 'Isso está certo arnaldo'

    }

}
