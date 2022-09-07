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
import br.com.doistech.utils.ErrorUi
import br.com.doistech.utils.Utils
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Executions
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

    List<String> statusOrderSalesList = ['Pendente', 'Em produção', 'Concluído', 'Entregue']

    @Init
    public void init() {
        orderSalesProductService = InjectUtils.getBean('orderSalesProductService')
        methodPaymentService = InjectUtils.getBean('methodPaymentService')
        orderSalesService = InjectUtils.getBean('orderSalesService')


        session = Sessions.getCurrent()

        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        defaultFromAndToDateMonth()

        returnListOrderSales()
//        orderSalesList = orderSalesService.getOpenOrderSalesList( statusOrderSalesList, company)

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
    void searchOrderSales(){
        orderSalesList = orderSalesService.getOpenOrderSalesList(statusOrderSalesList, company,
                searchMonthDate.orderSalesFromDate, searchMonthDate.orderSalesToDate)
    }

    @Command
    @NotifyChange(['*'])
    void editOrderSales(){
        Messagebox.show("Ops, homens trabalhando! Assim que estiver pronto te acaminharemos uma notificação", "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
//        Map args = [:]
//        args.action = 'edit'
//        args.codePage = 'order'
//        args.orderSales = orderSales
//        BindUtils.postGlobalCommand(null, null, "navagationPageOrderSales", args)
    }

    @Command
    void printOS(){
        String urlRedirect = "/admin/common/reports/serviceOrder.zul?orderNumber=${orderSales.id.toString()}"
        Executions.getCurrent().sendRedirect(urlRedirect, "_blank")
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
//        searchMonthDate.orderSalesFromDate,
//        searchMonthDate.orderSalesToDate
        orderSalesList = orderSalesService.getOpenOrderSalesList(statusOrderSalesList, company)
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
    void trucar(){

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
                totalOrders = totalOrders - mpos.methodPaymentOrderSales.valuePayment
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
            orderSales.updatedBy = user.username
            orderSales.dateUpdated = new Date()

            if(orderSales.orderStatus == 'Cancelado'){
                orderSales.deleteBy = user.username
                orderSales.dateDelete = new Date()
                orderSales.isDelete = true
            }

            println "Total: ${totalOrders} - Arredondando: ${Math.round(totalOrders)}"

            if(orderSales.paymentStatus == 'Pendente' && Math.round(totalOrders) == 0){
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
