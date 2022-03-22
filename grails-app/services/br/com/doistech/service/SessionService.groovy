package br.com.doistech.service

import br.com.doistech.domain.Account
import br.com.doistech.domain.Company
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.PackageCompany
import br.com.doistech.domain.User
import grails.transaction.Transactional
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

@Transactional
class SessionService {

    PackageComerceService packageComerceService
    AccountService accountService

    public void startSessionResgistrationAccount(User user, Account account, Company company, PackageComerce packageComerce){
        Session session = Sessions.getCurrent()
        session.setAttribute("user", user)
        session.setAttribute("account", account)
        session.setAttribute("company", company)
        session.setAttribute("packageComerce", packageComerce)
    }

    public void startSessionLogin(User user){
        Account account = accountService.getAccountByUser(user)
        PackageCompany packageCompany = packageComerceService.getPackageCompany(account.company)

        Session session = Sessions.getCurrent()
        session.setAttribute("user", user)
        session.setAttribute("account", account)
        session.setAttribute("company", account.company)
        session.setAttribute("packageCompany", packageCompany)
        session.setAttribute("packageComerce", packageCompany.packageComerce)
    }


    public void removeSessionLogout(User user){
        Session session = Sessions.getCurrent()
        session.removeAttribute("user")
        session.removeAttribute("account")
        session.removeAttribute("company")
        session.removeAttribute("packageCompany")
        session.removeAttribute("packageComerce")
    }
}
