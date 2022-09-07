package br.com.doistech.viewmodels

import br.com.doistech.domain.*
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.MethodPaymentService
import br.com.doistech.service.OrderSalesProductService
import br.com.doistech.service.OrderSalesService
import br.com.doistech.utils.ErrorUi
import br.com.doistech.utils.Utils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class ClosedOrdersVM {
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
    ErrorUi errorUi
    OrderSales orderSales
    OrderSalesProduct orderSalesProduct
    StatusOrderSales statusOrderSalesSelect

    // Variables
    Date orderSalesFromDate = new Date()
    Date orderSalesToDate = new Date()
    Boolean visibleForm = false
    String titleFormOrderSales
    Double remainsPay = 0
    Double totalOrders = 0

    //Maps
    Map searchMonthDate = [:]

    List<String> statusOrderSalesList = ['Concluído', 'Entregue']


    @Init
    public void init() {
        orderSalesProductService = InjectUtils.getBean('orderSalesProductService')
        methodPaymentService = InjectUtils.getBean('methodPaymentService')
        orderSalesService = InjectUtils.getBean('orderSalesService')


        session = Sessions.getCurrent()

        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        defaultFromAndToDateMonth()

        orderSalesList = orderSalesService.getClosedOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate, statusOrderSalesList, company)

        methodPaymentOrderSalesMapList = methodPaymentService.getMethodPaymentOrderList(company)
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
        statusOrderSalesSelect = statusOrdemSalesList.find{ it.status == 'Selecione o Status'}
    }

    @Command
    @NotifyChange(['*'])
    void searchOrderSales(String status){
        orderSalesList = orderSalesService.getClosedOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate, statusOrderSalesList, company)
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
        orderSalesList = orderSalesService.getClosedOrderSalesList(searchMonthDate.orderSalesFromDate,
                searchMonthDate.orderSalesToDate, statusOrderSalesList, company)
        visibleForm = false
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

    }

    @Command
    @NotifyChange(['*'])
    void save() {
        errorUi = new ErrorUi()
        Map resultUpdated = [:]

        if(orderSales.orderStatus == statusOrderSalesSelect.status){
            errorUi.addError('statusEquals', 'O novo Status pedido não pode ser igual ao Status atual')
            Messagebox.show("O Novo Status Pedido' não pode ser igual ao 'Status Pedido Atual'.",
                    "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
        }

        if(!errorUi.hasError){
            orderSales.orderStatus = statusOrderSalesSelect.status

            if(orderSales.paymentStatus == 'Pendente' && Utils.truncar(totalOrders) == 0){
                orderSales.paymentStatus = 'Pago'
            }

            Double sumMethodPaymentOrderSales = 0.0

            methodPaymentOrderSalesMapList.each { mpos ->
                sumMethodPaymentOrderSales += mpos.methodPaymentOrderSales.valuePayment
            }

            if(methodPaymentOrderSalesMapList.size() > 0 && sumMethodPaymentOrderSales > 0){
                resultUpdated = orderSalesService.updatedStatusOrderSales(orderSales, methodPaymentOrderSalesMapList)
            } else{
                resultUpdated = orderSalesService.updatedStatusOrderSales(orderSales)
            }

            if(resultUpdated.status == 0){
                orderSales = resultUpdated.orderSales
            } else {
                Messagebox.show("Ops, ocorreu um erro na atualização do seu pedido, tente novamente mais tarde!",
                        "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
            }
        }
    }

}
