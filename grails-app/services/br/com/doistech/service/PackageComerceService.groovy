package br.com.doistech.service

import br.com.doistech.domain.Company
import br.com.doistech.domain.PackageComerce
import br.com.doistech.domain.PackageCompany
import grails.transaction.Transactional
import org.hibernate.FetchMode

@Transactional
class PackageComerceService {

    List<PackageComerce> getPackageComerceList(){
        PackageComerce.withTransaction {
            return PackageComerce.createCriteria().list {}
        }
    }

    Map calculationPeriodPackage(Date dateCurrent, Long months, Long days = null){
        Calendar fromCal = Calendar.getInstance()
        Calendar toCal = Calendar.getInstance()
        fromCal.setTime(dateCurrent)
        toCal.setTime(dateCurrent)

        Map periodDateMap = [:]
        fromCal.set(Calendar.HOUR, 0)
        fromCal.set(Calendar.MINUTE, 0)
        fromCal.set(Calendar.SECOND, 0)
        periodDateMap.fromDate = fromCal.getTime()

        if(days){
            toCal.add(Calendar.DAY_OF_MONTH, days.toString().toInteger())
            toCal.set(Calendar.HOUR, 23)
            toCal.set(Calendar.MINUTE, 59)
            toCal.set(Calendar.SECOND, 59)
            periodDateMap.toDate = toCal.getTime()
        } else{
            toCal.add(Calendar.MONTH, months.toString().toInteger())
            toCal.set(Calendar.HOUR, 23)
            toCal.set(Calendar.MINUTE, 59)
            toCal.set(Calendar.SECOND, 59)
            periodDateMap.toDate = toCal.getTime()
        }
        return periodDateMap
    }

    public PackageCompany getPackageCompany(Company company){
        PackageCompany packageCompany
        PackageCompany.withTransaction {
            packageCompany = PackageCompany.createCriteria().get {
                eq('company',company)
                eq('packageActive',true)
                le('fromDate', new Date())
                ge('toDate', new Date())
                fetchMode("packageComerce", FetchMode.JOIN)
                fetchMode("company", FetchMode.JOIN)
            }
        }

        return packageCompany
    }

}
