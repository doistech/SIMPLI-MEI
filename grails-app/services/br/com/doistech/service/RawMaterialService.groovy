package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.RawMaterial
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional

@Transactional
class RawMaterialService {

    List<RawMaterial> getRawMaterialList(Company company){
        List<RawMaterial> rawMaterialList = []

        RawMaterial.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                eq('company', company)
            }
        }

        return rawMaterialList
    }

    List<RawMaterial> searchRawMaterial(String name, Company company){
        List<RawMaterial> rawMaterialList = []

        RawMaterial.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                like('name', '%' + name + '%')
                eq('company', company)
            }
        }
        return rawMaterialList
    }

    List<Cliente> getRawMaterialByCompany(Company company){
        List<RawMaterial> rawMaterialList = []
        Cliente.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                eq('company', company)
            }
        }
        return rawMaterialList
    }

    public ErrorUi validationRawMaterial(RawMaterial rawMaterial){
        ErrorUi errorUi = new ErrorUi()

        if(!rawMaterial.name){
            errorUi.addError('name', 'Por favor preencha o nome da matria prima')
        }

        if(!rawMaterial.price){
            errorUi.addError('price', 'Por favor preencha o preço da materia prima')
        }

        return errorUi
    }

    public Boolean save(RawMaterial rawMaterial, Company company, String username) {
        rawMaterial.dateCreated = new Date()
        rawMaterial.dateUpdated = new Date()

        rawMaterial.createdBy = username
        rawMaterial.updatedBy = username

        rawMaterial.company = company

        println 'Estou aqui tentando salvar'
        RawMaterial.withTransaction {
            try{
                rawMaterial.save(flush:true)
                println 'Teste'
            } catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }
        return true
    }
}
