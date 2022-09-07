package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.Product
import br.com.doistech.domain.ProductRawMaterial
import br.com.doistech.domain.RawMaterial
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional

@Transactional
class ProductRawMaterialService {

    List<ProductRawMaterial> getProductRawMaterialAndProductList(Company company, Product product){
        List<ProductRawMaterial> productRawMaterialList = []

        ProductRawMaterial.withTransaction {
            productRawMaterialList = ProductRawMaterial.createCriteria().list {
                eq('company', company)
                eq('product', product)
                eq('isExclud', false)
            }
        }

        return productRawMaterialList
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

    List<RawMaterial> getRawMaterialByCompany(Company company){
        List<RawMaterial> rawMaterialList = []
        RawMaterial.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                eq('company', company)
            }
        }
        return rawMaterialList
    }

    List<ProductRawMaterial> getProductRawMaterialCompanyAndProduct(Company company, Product product){
        List<ProductRawMaterial> productRawMaterialList = []
        ProductRawMaterial.withTransaction {
            productRawMaterialList = ProductRawMaterial.createCriteria().list {
                eq('product', product)
                eq('company', company)
            }
        }
        return productRawMaterialList
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

    public Boolean save(ProductRawMaterial productRawMaterial, Company company, String username) {
        productRawMaterial.dateCreated = new Date()
        productRawMaterial.dateUpdated = new Date()

        productRawMaterial.createdBy = username
        productRawMaterial.updatedBy = username
        productRawMaterial.excludBy = ''

        productRawMaterial.company = company

        ProductRawMaterial.withTransaction {
            try{
                productRawMaterial.save(flush:true)
            } catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }

        return true
    }
}
