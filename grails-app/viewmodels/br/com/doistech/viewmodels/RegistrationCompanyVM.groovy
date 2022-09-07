package br.com.doistech.viewmodels

import br.com.doistech.domain.Account
import br.com.doistech.domain.Company
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.User
import br.com.doistech.service.PackageComerceService
import br.com.doistech.service.RegistrationService
import br.com.doistech.utils.ErrorUi
import br.com.doistech.service.InjectUtils
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class RegistrationCompanyVM {
    RegistrationService registrationService
    PackageComerceService packageComerceService

    public static final String STEP01 = '/admin/registration/_step01.zul'
    public static final String STEP02 = '/admin/registration/_step02.zul'
    public static final String STEP03 = '/admin/registration/_step03.zul'
    public static final String STEP04 = '/admin/registration/_step04.zul'

    Session session

    String stepCurrent

    User user
    Account account
    Company company

    ErrorUi errorUi

    PackageComerce packageComerce
//    List<PackageComerce> packageComerceList = []

    @Init
    void init() {
        registrationService = InjectUtils.getBean('registrationService')
        packageComerceService = InjectUtils.getBean('packageComerceService')
        setup()
    }

    @NotifyChange(['*'])
    public void setup(){
        session = Sessions.getCurrent()

        stepCurrent = STEP01
        user = new User()
        account = new Account()
        company = new Company()
        errorUi = new ErrorUi()

        packageComerce = (PackageComerce) session.getAttribute('packageComerceSelect')

//        packageComerceList = packageComerceService.getPackageComerceList()
    }

    @Command
    @NotifyChange(['stepCurrent', 'errorUi'])
    public void nextStep01(){
        errorUi = registrationService.validationUser(user)
        if(stepCurrent == STEP01 && !errorUi.hasError){
            stepCurrent = STEP02
        }

        println stepCurrent
    }

    @Command
    @NotifyChange(['stepCurrent', 'errorUi', 'company'])
    public void nextStep02(){
        errorUi = registrationService.validationCompany(company)
        if(stepCurrent == STEP02 && !errorUi.hasError){
            stepCurrent = STEP03
        }

        println stepCurrent
    }

    @Command
    @NotifyChange(['stepCurrent', 'errorUi'])
    public void registrationSave(){
        Boolean validationSave = false
        errorUi = registrationService.validationAccount(account)
        if(!errorUi.hasError){
            validationSave = registrationService.save(user, company, account, packageComerce)
        }

        if(validationSave){
            Executions.sendRedirect("/admin/home/index.zul")
        }

    }

}
