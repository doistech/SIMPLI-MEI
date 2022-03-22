package br.com.doistech.viewmodels

import br.com.doistech.domain.PackageComerce
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.PackageComerceService
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class HomeVM {

    PackageComerceService packageComerceService

    Session session

    List<PackageComerce> packageComerceList = []

    PackageComerce packageComerce

    @Init
    void init() {
        packageComerceService = InjectUtils.getBean('packageComerceService')
        packageComerceList = packageComerceService.getPackageComerceList()
    }

    @Command
    @NotifyChange([])
    void selectPackageComerce(@BindingParam("packageComerceSelect") PackageComerce packageComerceSelect){
        Session session = Sessions.getCurrent()
        session.setAttribute("packageComerceSelect", packageComerceSelect)
        Executions.sendRedirect("/admin/registration/index.zul")
    }
}
