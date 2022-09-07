package br.com.doistech.viewmodels

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.MethodPayment
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.OrderSalesProduct
import br.com.doistech.domain.Product
import br.com.doistech.domain.User
import br.com.doistech.service.ClientService
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.JsonManipulationService
import br.com.doistech.service.MethodPaymentService
import br.com.doistech.service.OrderSalesService
import br.com.doistech.service.ProductService
import br.com.doistech.utils.ErrorUi
import br.com.doistech.utils.Utils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions
import org.zkoss.zk.ui.event.Event

import java.math.RoundingMode
import java.text.DecimalFormat

class OrderVM {
    ClientService clientService
    ProductService productService
    OrderSalesService orderSalesService
    MethodPaymentService methodPaymentService
    JsonManipulationService jsonManipulationService

    Session session

    User user
    Company company

    Boolean visibleForm = false

    ErrorUi errorUi = new ErrorUi()

    OrderSales orderSales = new OrderSales()
    OrderSalesProduct orderSalesProduct = new OrderSalesProduct()

    Double quantity = 1

    // TODO: Tentar achar utilizacão
    Double totalOrders = 0
    Double subtotalOrders = 0

    Cliente clienteSelect
    Product productSelect

    MethodPayment methodPaymentPix
    MethodPayment methodPaymentMoney
    MethodPayment methodPaymentCredit
    MethodPayment methodPaymentDebit

    Date orderSalesDate = new Date()

    List<Product> productList = []
    List<Cliente> clienteList = []
    List<OrderSalesProduct> orderSalesProductList = []
    List<OrderSalesProduct> orderSalesProductDeleteList = []
    List<Map> methodPaymentOrderSalesList = []


    Map butonMethodPaymentMap = [signal  : [action: false, class: 'linkButton'],
                                 total   : [action: true, class: 'activeButtonPayment'],
                                 delivery: [action: false, class: 'linkButton']]

    @Init
    public void init() {
        clientService = InjectUtils.getBean('clientService')
        productService = InjectUtils.getBean('productService')
        orderSalesService = InjectUtils.getBean('orderSalesService')
        methodPaymentService = InjectUtils.getBean('methodPaymentService')
        jsonManipulationService = InjectUtils.getBean('jsonManipulationService')

        session = Sessions.getCurrent()
        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        clienteList = clientService.getClienteByCompany(company)
        productList = productService.getProductByCompany(company)
        methodPaymentOrderSalesList = methodPaymentService.getMethodPaymentOrderList(company)

        clienteSelect = clienteList.find { it.name == 'Cliente Balcão' }


    }

    @Command
    @NotifyChange(['*'])
    void actionMethodPayment(@BindingParam("methodPaymentSelect") String methodPaymentSelect) {
        butonMethodPaymentMap = [signal  : [action: false, class: 'linkButton'],
                                 total   : [action: false, class: 'linkButton'],
                                 delivery: [action: false, class: 'linkButton']]

        if (methodPaymentSelect == 'signal') {

            butonMethodPaymentMap.signal.action = true
            butonMethodPaymentMap.signal.class = 'activeButtonPayment'
        } else if (methodPaymentSelect == 'total') {

            butonMethodPaymentMap.total.action = true
            butonMethodPaymentMap.total.class = 'activeButtonPayment'
        } else if (methodPaymentSelect == 'delivery') {

            butonMethodPaymentMap.delivery.action = true
            butonMethodPaymentMap.delivery.class = 'activeButtonPayment'
        }
    }

    @Command
    @NotifyChange(['*'])
    void addProductList() {
        if (quantity < 1) {
            Messagebox.show("Por favor, insira a quantidade produto.",
                    'Simplifica MEI', 0, Messagebox.ERROR)
        } else if (!productSelect) {
            Messagebox.show("Por favor, selecione o produto.",
                    'Simplifica MEI', 0, Messagebox.ERROR)
        } else {
            orderSalesProduct = new OrderSalesProduct()

            orderSalesProduct.product = productSelect
            orderSalesProduct.quantityItem = quantity

            if (productSelect.wholesaleQuantity > 0 && orderSalesProduct.quantityItem >= productSelect.wholesaleQuantity) {
                orderSalesProduct.priceItem = productSelect.wholesalePrice
                orderSalesProduct.totalPriceItem = orderSalesProduct.quantityItem * orderSalesProduct.priceItem
            } else {
                orderSalesProduct.priceItem = productSelect.salesPrice
                orderSalesProduct.totalPriceItem = orderSalesProduct.quantityItem * orderSalesProduct.priceItem
            }
            orderSalesProductList.add(orderSalesProduct)
            subtotalOrders()

            // TODO: Como zerar o combobox zk
            // productSelect = null
        }
    }

    void subtotalOrders() {
        orderSales.totalOrder = 0
        orderSales.subtotalOrder = 0

        orderSalesProductList.each { OrderSalesProduct osp ->
            orderSales.totalOrder += osp.totalPriceItem
            orderSales.subtotalOrder += osp.totalPriceItem
        }

        totalOrders = orderSales.totalOrder
    }

    @Command
    @NotifyChange(['*'])
    void paymentControl(@BindingParam("methodPaymentOrderSales") def methodPaymentOrderSales){

        totalOrders = orderSales.totalOrder

        methodPaymentOrderSalesList.each { mpos ->
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
    void calculationDiscount() {
        orderSales.totalOrder = (orderSales.subtotalOrder - orderSales.discount) + orderSales.addition
        totalOrders = orderSales.totalOrder
        println "TotalOrders: ${orderSales.totalOrder} - SubTotalOrders: ${orderSales.subtotalOrder} - Discount: ${orderSales.discount}"
    }

    @Command
    @NotifyChange(['*'])
    void calculationAddition() {
        orderSales.totalOrder = (orderSales.subtotalOrder - orderSales.discount) + orderSales.addition
        totalOrders = orderSales.totalOrder
        println "TotalOrders: ${orderSales.totalOrder} - SubTotalOrders: ${orderSales.subtotalOrder} - Addition: ${orderSales.addition}"
    }

    @Command
    @NotifyChange(['*'])
    void finishOrderSales() {
        errorUi = new ErrorUi()

        String urlRedirect

        //Validar se tem produtos no pedido
        if(orderSalesProductList.size() == 0){
            errorUi.addError('productListEmpty', 'Por favor, é necessária ter ao menos um produto no seu pedido!')
            Messagebox.show('Por favor, é necessária ter ao menos um produto no seu pedido!', "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
        }

        Double sumMethodPaymentOrderSales = 0.0

        methodPaymentOrderSalesList.each { mpos ->
            sumMethodPaymentOrderSales += mpos.methodPaymentOrderSales.valuePayment
        }

        if(sumMethodPaymentOrderSales == 0.0 && butonMethodPaymentMap.delivery.action != true){
            errorUi.addError('methodPaymentNull', 'Por favor, informe as formas de pagamento!')
            Messagebox.show('Por favor, informe as formas de pagamento!', "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
        }

        if(!errorUi.hasError) {
            if (butonMethodPaymentMap.signal.action) {
                if (sumMethodPaymentOrderSales > 0) {
                    if (sumMethodPaymentOrderSales == orderSales.totalOrder) {
                        orderSales.orderStatus = 'Pendente'
                        orderSales.paymentStatus = 'Pago'
                    } else if (sumMethodPaymentOrderSales < orderSales.totalOrder) {
                        orderSales.orderStatus = 'Pendente'
                        orderSales.paymentStatus = 'Pendente'
                    }
                    orderSales.methodPaymentOrderSales = 'Signal'
                    orderSales = orderSalesService.finishOrderSales(orderSales, user, company, clienteSelect, orderSalesProductList, methodPaymentOrderSalesList)
                    if(orderSales){
                        urlRedirect = "/admin/common/reports/serviceOrder.zul?orderNumber=" + orderSales.id.toString()
                        clearOrderSales()
                        println urlRedirect
                        Executions.getCurrent().sendRedirect(urlRedirect, "_blank")
                    }
                } else {
                    Messagebox.show("Por favor, informe os meios de pagamento do pedido!", "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
                }
            } else if (butonMethodPaymentMap.total.action) {
                if (sumMethodPaymentOrderSales == Utils.truncar(orderSales.totalOrder)) {
                    orderSales.orderStatus = 'Pendente'
                    orderSales.paymentStatus = 'Pago'
                    orderSales.methodPaymentOrderSales = 'Total'
                    orderSales = orderSalesService.finishOrderSales(orderSales, user, company, clienteSelect, orderSalesProductList, methodPaymentOrderSalesList)
                    if(orderSales){
                        urlRedirect = "/admin/common/reports/serviceOrder.zul?orderNumber=" + orderSales.id.toString()
                        clearOrderSales()
                        println urlRedirect
                        Executions.getCurrent().sendRedirect(urlRedirect, "_blank")
                    }
                } else if (sumMethodPaymentOrderSales > Utils.truncar(orderSales.totalOrder)) {
                    Messagebox.show("O valor de pagamento não pode ser maior do que o valor do pedido", "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
                } else {
                    Messagebox.show("Por favor, informe os meios de pagamento do pedido!", "SI MEI", Messagebox.OK, Messagebox.EXCLAMATION)
                }
            } else if (butonMethodPaymentMap.delivery.action) {
                orderSales.orderStatus = 'Pendente'
                orderSales.paymentStatus = 'Pendente'
                orderSales.methodPaymentOrderSales = 'Delivery'
                orderSales = orderSalesService.finishOrderSales(orderSales, user, company, clienteSelect, orderSalesProductList, methodPaymentOrderSalesList)
                if(orderSales){
                    urlRedirect = "/admin/common/reports/serviceOrder.zul?orderNumber=" + orderSales.id.toString()
                    clearOrderSales()
                    println urlRedirect
                    Executions.getCurrent().sendRedirect(urlRedirect, "_blank")
                }
            }
        }
    }

    @Command
    @NotifyChange(['*'])
    void clearOrderSales(){
        orderSales = new OrderSales()

        clienteList = clientService.getClienteByCompany(company)
        clienteSelect = clienteList.find { it.name == 'Cliente Balcão' }

        orderSalesProductList.clear()
        methodPaymentOrderSalesList = methodPaymentService.getMethodPaymentOrderList(company)
        productList = productService.getProductByCompany(company)
        productSelect = null
        totalOrders = 0
    }

    @Command
    @NotifyChange(['*'])
    void deleteItemSalesOrders(@BindingParam("osp") OrderSalesProduct osp) {
        orderSalesProductList.remove(osp)
        orderSalesProductDeleteList.add(osp)
        subtotalOrders()

    }
}



