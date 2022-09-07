package br.com.doistech.viewmodels

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.User
import br.com.doistech.service.ClientService
import br.com.doistech.service.InjectUtils
import br.com.doistech.utils.ErrorUi
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class ClienteVM {

    ClientService clientService

    Session session

    User user
    Company company

    String searchName

    Cliente cliente = new Cliente()

    List<Cliente> clienteList = []

    ErrorUi errorUi

    Boolean visibleForm = false

    @Init
    void init(){
        clientService = InjectUtils.getBean('clientService')

        session = Sessions.getCurrent()
        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        clienteList = clientService.getClienteByCompany(company)
    }

    @Command
    @NotifyChange(['*'])
    void searchCliente(){
        clienteList = clientService.searchCliente(searchName, company)
    }

    @Command
    @NotifyChange(['*'])
    void newCliente() {
        cliente = new Cliente()
        visibleForm = true
    }

    @Command
    @NotifyChange(['*'])
    void returnListCliente() {
        cliente = new Cliente()
        clienteList = clientService.getClienteByCompany(company)
        visibleForm = false
    }

    @Command
    @NotifyChange(['*'])
    void save(){
        errorUi = clientService.validationCliente(cliente)

        Boolean saveError = false
        if(!errorUi.hasError){
            saveError = clientService.save(cliente, company, user.username)

            if(!saveError){
                Messagebox.show("Erro na inclusão do cliente. Tente mais tarde!",
                        'Simplifica MEI', 0,  Messagebox.ERROR)
                visibleForm = false
            } else {
                visibleForm = true
                cliente = new Cliente()
            }
        }
    }

    @Command
    @NotifyChange(['*'])
    void selectClientGrid(@BindingParam("clienteSelect") Cliente clienteSelect){
        cliente = clienteSelect
        visibleForm = true
    }

}
