package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.PackageCompany
import br.com.doistech.domain.User
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional
import org.hibernate.FetchMode

@Transactional
class ClientService {

    public ErrorUi validationCliente (Cliente cliente){
        ErrorUi errorUi = new ErrorUi()

        if(!cliente.name){
            errorUi.addError('ClientNome', 'Por favor preencha o nome do cliente')
        }

        return errorUi
    }

    public Boolean save(Cliente cliente, Company company, String username) {
        cliente.dateCreated = new Date()
        cliente.dateUpdated = new Date()

        cliente.createdBy = username
        cliente.updatedBy = username

        cliente.company = company
//        cliente.company = Company.get(7.toLong())

        Cliente.withTransaction {
            try{
                cliente.save(flush:true)
            } catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }
        return true
    }

    List<Cliente> getClienteByCompany(Company company){
        List<Cliente> clienteList = []
        Cliente.withTransaction {
            clienteList = Cliente.createCriteria().list {
                eq('company', company)
                order("name", "asc")
            }
        }
        return clienteList
    }

    Cliente getClienteById(Long id){
        return Cliente.createCriteria().get {
            eq('id', id)
            fetchMode("cliente", FetchMode.JOIN)
        }
    }

    List<Cliente> searchCliente(String name, Company company){
        List<Cliente> clienteList = []

        Cliente.withTransaction {
            clienteList = Cliente.createCriteria().list {
                like('name', '%' + name + '%')
                eq('company', company)
                order("name", "asc")
            }
        }
        return clienteList
    }

}
