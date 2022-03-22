package br.com.doistech.service

import br.com.doistech.domain.Company
import br.com.doistech.domain.User
import grails.transaction.Transactional
import org.hibernate.FetchMode

@Transactional
class CompanyService {

    public Company getCompany(String taxId){
        Company company

        Company.withTransaction {
            company = Company.createCriteria().get {
                eq('taxid', taxId)
            }
        }

        return company
    }


}
