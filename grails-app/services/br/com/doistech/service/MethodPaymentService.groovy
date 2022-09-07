package br.com.doistech.service

import br.com.doistech.domain.Company
import br.com.doistech.domain.MethodPayment
import br.com.doistech.domain.MethodPaymentOrderSales
import br.com.doistech.domain.OrderSales
import grails.transaction.Transactional

@Transactional
class MethodPaymentService {

    List<Map> getMethodPaymentOrderList(Company company){
        List<MethodPayment> methodPaymentList = []
        List<Map> methodPaymentOrderSalesList = []

        methodPaymentList = getMethodPaymentList(company)

        methodPaymentList.each { MethodPayment methodPayment ->
            MethodPaymentOrderSales methodPaymentOrderSales = new MethodPaymentOrderSales()

            methodPaymentOrderSales.methodPayment = methodPayment
            methodPaymentOrderSales.company = company

            methodPaymentOrderSalesList.add([name:methodPayment.name, methodPaymentOrderSales: methodPaymentOrderSales])
        }

        return methodPaymentOrderSalesList
    }

    MethodPayment getMethodPayment(Company company, String name){
        MethodPayment methodPayment

        MethodPayment.withTransaction {
            methodPayment = MethodPayment.createCriteria().get {
                eq('company', company)
                eq('name', name)
            }
        }

        return methodPayment
    }

    List<MethodPayment> getMethodPaymentList(Company company){
        MethodPayment.withTransaction {
            return MethodPayment.createCriteria().list {
                eq('company', company)
            }
        }
    }

    Double sumMethodPaymentOrderSalesList(List<Map> methodPaymentOrderSalesList){
        Double totalOrderPayment = 0
        methodPaymentOrderSalesList.each { mpos ->
            totalOrderPayment += mpos.methodPaymentOrderSales.valuePayment
        }
        return totalOrderPayment
    }
}
