package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.MethodPayment
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.StatusOrderSales
import grails.transaction.Transactional

@Transactional
class SetupService {

    void setup() {
        PackageComerce packageFree
        PackageComerce packageMonth
        PackageComerce packageMonth6
        PackageComerce packageYear

        PackageComerce.withTransaction {

            packageFree = getPackageComerce('SI MEI - TESTE')

            if (!packageFree) packageFree = new PackageComerce()
            packageFree.name = 'SI MEI - TESTE'
            packageFree.label = '15 DIAS'
            packageFree.price = 0
            packageFree.priceTotal = 0
            packageFree.description = 'TESTE'
            packageFree.dateCreated = new Date()
            packageFree.dateUpdated = new Date()
            packageFree.durationDay = 15
            packageFree.countUser = 1
            packageFree.save(flush: true)


            packageMonth = getPackageComerce('SI MEI - MENSAL')

            if (!packageMonth) packageMonth = new PackageComerce()
            packageMonth.name = 'SI MEI - MENSAL'
            packageMonth.label = 'POR MÊS'
            packageMonth.price = 25
            packageMonth.priceTotal = 25
            packageMonth.description = 'MENSAL'
            packageMonth.dateCreated = new Date()
            packageMonth.dateUpdated = new Date()
            packageMonth.durationMonths = 1
            packageMonth.countUser = 2
            packageMonth.save(flush: true)


            packageMonth6 = getPackageComerce('SI MEI - SEMESTRAL')

            if (!packageMonth6) packageMonth6 = new PackageComerce()
            packageMonth6.name = 'SI MEI - SEMESTRAL'
            packageMonth6.label = 'POR SEMESTRE'
            packageMonth6.price = 22.5
            packageMonth6.priceTotal = 135
            packageMonth6.description = 'SEMESTRAL'
            packageMonth6.dateCreated = new Date()
            packageMonth6.dateUpdated = new Date()
            packageMonth6.durationMonths = 6
            packageMonth6.countUser = 4
            packageMonth6.save(flush: true)

            packageYear = getPackageComerce('SI MEI - ANUAL')

            if (!packageYear) packageYear = new PackageComerce()
            packageYear.name = 'SI MEI - ANUAL'
            packageYear.label = 'POR ANO'
            packageYear.price = 20.83
            packageYear.priceTotal = 250
            packageYear.description = 'ANUAL'
            packageYear.dateCreated = new Date()
            packageYear.dateUpdated = new Date()
            packageYear.durationMonths = 12
            packageYear.countUser = 8
            packageYear.save(flush: true)

        }

        Cliente cliente
        List<Company> companyList = []

        Company.withTransaction {
            companyList = Company.createCriteria().list {}
        }

        Cliente.withTransaction {
            companyList.each { Company company ->
                cliente = Cliente.findByNameAndCompany('Cliente Balcão', company)
                if (!cliente) {
                    cliente = new Cliente()
                    cliente.name = 'Cliente Balcão'
                    cliente.company = company
                    cliente.save(flush: true)
                }
            }
        }

        MethodPayment pix
        MethodPayment money
        MethodPayment credit
        MethodPayment debt

        MethodPayment.withTransaction {
            companyList.each { Company company ->
                pix = MethodPayment.findByNameAndCompany('PIX', company)
                if (!pix) {
                    pix = new MethodPayment()
                    pix.name = 'PIX'
                    pix.description = 'Pagamento via pix'
                    pix.company = company
                    pix.save(flush: true)
                }

                money = MethodPayment.findByNameAndCompany('Dinheiro', company)
                if (!money) {
                    money = new MethodPayment()
                    money.name = 'Dinheiro'
                    money.description = 'Pagamento via Dinheiro'
                    money.company = company
                    money.save(flush: true)
                }

                credit = MethodPayment.findByNameAndCompany('Crédito', company)
                if (!credit) {
                    credit = new MethodPayment()
                    credit.name = 'Crédito'
                    credit.description = 'Pagamento via Crédito'
                    credit.company = company
                    credit.save(flush: true)
                }

                debt = MethodPayment.findByNameAndCompany('Débito', company)
                if (!debt) {
                    debt = new MethodPayment()
                    debt.name = 'Débito'
                    debt.description = 'Pagamento via Débito'
                    debt.company = company
                    debt.save(flush: true)
                }
            }
        }

        StatusOrderSales statusOrderSales01
        StatusOrderSales statusOrderSales02
        StatusOrderSales statusOrderSales03
        StatusOrderSales statusOrderSales04
        StatusOrderSales statusOrderSales05

        StatusOrderSales.withTransaction {
            companyList.each { Company company ->

                statusOrderSales01 = StatusOrderSales.findByStatusAndCompany('Selecione o Status', company)
                if (!statusOrderSales01) {
                    statusOrderSales01 = new StatusOrderSales()
                    statusOrderSales01.status = 'Selecione o Status'
                    statusOrderSales01.company = company
                    statusOrderSales01.save(flush: true)
                }

                statusOrderSales02 = StatusOrderSales.findByStatusAndCompany('Pendente', company)
                if (!statusOrderSales02) {
                    statusOrderSales02 = new StatusOrderSales()
                    statusOrderSales02.status = 'Pendente'
                    statusOrderSales02.company = company
                    statusOrderSales02.save(flush: true)
                }

                statusOrderSales03 = StatusOrderSales.findByStatusAndCompany('Em produção', company)
                if (!statusOrderSales03) {
                    statusOrderSales03 = new StatusOrderSales()
                    statusOrderSales03.status = 'Em produção'
                    statusOrderSales03.company = company
                    statusOrderSales03.save(flush: true)
                }

                statusOrderSales04 = StatusOrderSales.findByStatusAndCompany('Concluído', company)
                if (!statusOrderSales04) {
                    statusOrderSales04 = new StatusOrderSales()
                    statusOrderSales04.status = 'Concluído'
                    statusOrderSales04.company = company
                    statusOrderSales04.save(flush: true)
                }

                statusOrderSales05 = StatusOrderSales.findByStatusAndCompany('Entregue', company)
                if (!statusOrderSales05) {
                    statusOrderSales05 = new StatusOrderSales()
                    statusOrderSales05.status = 'Entregue'
                    statusOrderSales05.company = company
                    statusOrderSales05.save(flush: true)
                }
            }
        }
    }

    PackageComerce getPackageComerce(String name){
        return  PackageComerce.createCriteria().get {
            eq('name', name)
            maxResults(1)
        }
    }
}
