package br.com.doistech.service

import br.com.doistech.domain.Account
import br.com.doistech.domain.Company
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.PackageCompany
import br.com.doistech.domain.User
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional

@Transactional
class RegistrationService {

    PackageComerceService packageComerceService
    UserProfileService userProfileService
    CompanyService companyService
    SessionService sessionService




    public ErrorUi validationUser (User user){
        ErrorUi errorUi = new ErrorUi()

        User auxUser = userProfileService.getUser(user.username)

        if(auxUser){
            errorUi.addError('UserNotNull', 'Esse e-mail já existe em nossa base.')
        } else if(user.password != user.passwordConfirmation) {
            errorUi.addError('UserNotNull', 'As senhas não coincidem, por favor digite novamente.')
        }

        return errorUi
    }

    public ErrorUi validationCompany (Company company){
        ErrorUi errorUi = new ErrorUi()

        Company auxCompany = companyService.getCompany(company.taxid)

        if(auxCompany) {
            errorUi.addError('CompanyNotNull', 'Esse empresa já existe em nossa base.')
        }

        if(!company.nameFantasy) {
            errorUi.addError('NameFantasyNull', 'Por favor, preencha o Nome Fantasia da empresa.')
        }

        if(!company.taxid){
            errorUi.addError('taxidNull', 'Por favor, preencha o CPF/CNPJ da empresa.')
        }

        return errorUi
    }

    public ErrorUi validationAccount (Account account){
        ErrorUi errorUi = new ErrorUi()

//        Account auxAccount = companyService.getCompany(account.name)

        if(!account.name) {
            errorUi.addError('NameNull', 'Por favor, preencha o seu Nome.')
        }

        return errorUi
    }

    public Boolean save(User user, Company company, Account account, PackageComerce packageComerce){

        PackageCompany packageCompany = new PackageCompany()

        user.dateCreated = new Date()
        user.dateUpdated = new Date()

        company.dateCreated = new Date()
        company.dateUpdated = new Date()

        account.dateCreated = new Date()
        account.dateUpdated = new Date()

        packageCompany.dateCreated = new Date()
        packageCompany.dateUpdated = new Date()

        Map durationPackageMap = packageComerceService.calculationPeriodPackage(new Date(), packageComerce.durationMonths,
                packageComerce.durationDay)

        User.withTransaction {
            try{
                user.encryptPassword()
                user.save(flush:true)
                company.email = user.username
                company.save(flush:true)
                account.user = user
                account.company = company
                account.save(flush:true)

                packageCompany.company = company
                packageCompany.packageComerce = packageComerce
                packageCompany.fromDate = durationPackageMap.fromDate
                packageCompany.toDate = durationPackageMap.toDate
                packageCompany.packageActive = true
                packageCompany.type = 'Teste'
                packageCompany.save(flush:true)

                sessionService.startSessionResgistrationAccount(user, account, company)
            }catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }
        return true
    }

}
