package br.com.doistech.viewmodels

import br.com.doistech.domain.User
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.LoginService
import br.com.doistech.service.SessionService
import br.com.doistech.service.UserProfileService
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class LoginVM {

    LoginService loginService
    SessionService sessionService
    UserProfileService userProfileService

    Session session

    User user = new User()

    @Init
    public void init(){
        userProfileService = InjectUtils.getBean('userProfileService')
        loginService = InjectUtils.getBean('loginService')

        session = Sessions.getCurrent()

        User user = (User) session.getAttribute('user')

        if(user){
            Executions.sendRedirect("/admin/index.zul")
//             Executions.sendRedirect("/admin/login/auth.zul")
        }
    }

    @Command
    @NotifyChange(['*'])
    public void login(){
        Boolean hasLogin = loginService.login(user.username, user.password)

        if(hasLogin){
            Executions.sendRedirect("/admin/index.zul")
        } else {
            Messagebox.show("Login ou senha incorretos",
                    'Simplifica MEI', 0,  Messagebox.ERROR)
        }
    }

    public void logout(){
        sessionService.removeSessionLogout()
        Executions.sendRedirect("/admin/login/auth.zul")
    }
}
