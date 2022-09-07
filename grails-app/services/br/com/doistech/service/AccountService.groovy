package br.com.doistech.service

import br.com.doistech.domain.Account
import br.com.doistech.domain.Company
import br.com.doistech.domain.User
import grails.transaction.Transactional
import org.hibernate.FetchMode

@Transactional
class AccountService {

    public Company getAccount(String name){
        Account account

        Account.withTransaction {
            account = Account.createCriteria().get {
                eq('name', name)
            }
        }

        return account
    }

    public Account getAccountByUser(User user){
        Account account

        Account.withTransaction {
            account = Account.createCriteria().get {
                eq('user', user)
                fetchMode("company", FetchMode.JOIN)
                fetchMode("user", FetchMode.JOIN)
            }
        }

        return account
    }
}
