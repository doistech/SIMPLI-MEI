package br.com.doistech.viewmodels

import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.User
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.SessionService
import br.com.doistech.service.SetupService
import br.com.doistech.utils.ConstantPage
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

import javax.management.Notification

class IndexAdminVM {
    SessionService sessionService
    SetupService setupService

    Session session

    User user

    String currentPage
    @Init
    void init() {
        sessionService = InjectUtils.getBean('sessionService')
        setupService = InjectUtils.getBean('setupService')

        setupService.setup()

        session = Sessions.getCurrent()

//        currentPage = ConstantPage.CLIENT_APP

        user = (User) session.getAttribute('user')
        if(!user){
            Executions.sendRedirect("/admin/login/auth.zul")
        }

//        registrationService = InjectUtils.getBean('registrationService')

    }

    @Command
    @NotifyChange(['*'])
    public void logout(){
        sessionService.removeSessionLogout()
        Executions.sendRedirect("/admin/login/auth.zul")
    }


    @Command
    @NotifyChange(['*'])
    public void navagationPage(@BindingParam("codePage") String codePage){
        switch (codePage){
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


}
