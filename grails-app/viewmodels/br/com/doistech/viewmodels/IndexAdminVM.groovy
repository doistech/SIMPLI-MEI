package br.com.doistech.viewmodels

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.OrderSales
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.Product
import br.com.doistech.domain.User
import br.com.doistech.service.ClientService
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.OrderSalesService
import br.com.doistech.service.ProductService
import br.com.doistech.service.SessionService
import br.com.doistech.service.SetupService
import br.com.doistech.utils.ConstantPage
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Default
import org.zkoss.bind.annotation.GlobalCommand
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Li
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

import javax.management.Notification

class IndexAdminVM {
    SessionService sessionService
    SetupService setupService

    ClientService clientService
    ProductService productService
    OrderSalesService orderSalesService

    Session session

    User user
    Company company

    String currentPage

    List<Cliente> clienteList = []
    List<Product> productList = []
    List<OrderSales> orderSalesOpenList = []
    List<OrderSales> orderSalesClosedList = []

    List<String> statusOrderSalesClosedList = ['Concluído', 'Entregue']
    List<String> statusOrderSalesOpenList = ['Pendente', 'Em produção', 'Concluído', 'Entregue']

    Map dashboardCardHomeMap = [:]

    @Init
    void init() {
        sessionService = InjectUtils.getBean('sessionService')
        setupService = InjectUtils.getBean('setupService')
        clientService = InjectUtils.getBean('clientService')
        productService = InjectUtils.getBean('productService')
        orderSalesService = InjectUtils.getBean('orderSalesService')

        setupService.setup()

        session = Sessions.getCurrent()

        currentPage = ConstantPage.DASHBOARD_APP

        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

//        setupService.updatedProductsPrices(user, company)

        if (!user) {
            Executions.sendRedirect("/admin/login/auth.zul")
        }

        clienteList = clientService.getClienteByCompany(company)
        productList = productService.getProductByCompany(company)
        orderSalesOpenList = orderSalesService.getOpenOrderSalesList(statusOrderSalesOpenList, company)
        orderSalesClosedList = orderSalesService.getClosedOrderSalesList(statusOrderSalesClosedList, company)
        getDashboardCardHome()
    }

    @Command
    @NotifyChange(['*'])
    void getDashboardCardHome(){
        if(clienteList){
            dashboardCardHomeMap.clientCount = clienteList.size()
        }else{
            dashboardCardHomeMap.clientCount = 0
        }
        if(orderSalesOpenList) {
            dashboardCardHomeMap.orderOpenCount = orderSalesOpenList.size()
        }else{
            dashboardCardHomeMap.orderOpenCount = 0
        }
        if(orderSalesOpenList){
            dashboardCardHomeMap.orderClosedCount = orderSalesClosedList.size()
        }else{
            dashboardCardHomeMap.orderClosedCount = 0
        }
        if(productList){
            dashboardCardHomeMap.productCount = productList.size()
        }else{
            dashboardCardHomeMap.productCount = 0
        }
    }

    @Command
    @NotifyChange(['*'])
    public void logout() {
        sessionService.removeSessionLogout()
        Executions.sendRedirect("/admin/login/auth.zul")
    }


    @GlobalCommand
    @NotifyChange(['*'])
    void navagationPage(@BindingParam("codePage") String codePage) {
        switch (codePage) {
            case 'dashboard':
                currentPage = ConstantPage.DASHBOARD_APP
                break
            case 'account':
                currentPage = ConstantPage.ACCOUNT_APP
                break
            case 'users':
                currentPage = ConstantPage.USERS_APP
                break
            case 'cliente':
                currentPage = ConstantPage.CLIENT_APP
                break
            case 'rawMaterial':
                currentPage = ConstantPage.RAW_MATERIAL_APP
                break
            case 'product':
                currentPage = ConstantPage.PRODUCT_APP
                break
            case 'order':
                currentPage = ConstantPage.ORDER_APP
                break
            case 'openOrders':
                currentPage = ConstantPage.OPEN_ORDERS_APP
                break
            case 'completedOrders':
                currentPage = ConstantPage.COMPLETED_ORDERS_APP
                break
        }
    }

    @GlobalCommand
    @NotifyChange(['*'])
    void navagationPageOrderSales(@BindingParam("args") Map args) {
        String codePage = args.codePage
        if (codePage) {
            switch (codePage) {
//            case 'account':
//                currentPage = ConstantPage.ACCOUNT_APP
//                break
//            case 'users':
//                currentPage = ConstantPage.USERS_APP
//                break
//            case 'cliente':
//                currentPage = ConstantPage.CLIENT_APP
//                break
//            case 'rawMaterial':
//                currentPage = ConstantPage.RAW_MATERIAL_APP
//                break
//            case 'product':
//                currentPage = ConstantPage.PRODUCT_APP
//                break
                case 'order':
                    currentPage = ConstantPage.ORDER_APP
                    break
//            case 'openOrders':
//                currentPage = ConstantPage.OPEN_ORDERS_APP
//                break
//            case 'completedOrders':
//                currentPage = ConstantPage.COMPLETED_ORDERS_APP
//                break
            }
        }
    }


}
