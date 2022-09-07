package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.Product
import br.com.doistech.domain.ProductRawMaterial
import br.com.doistech.domain.RawMaterial
import br.com.doistech.domain.User
import br.com.doistech.utils.ErrorUi
import br.com.doistech.utils.Utils
import grails.transaction.Transactional
import org.hibernate.FetchMode
import org.zkoss.zhtml.Messagebox

@Transactional
class RawMaterialService {

    List<RawMaterial> getRawMaterialList(Company company){
        List<RawMaterial> rawMaterialList = []

        RawMaterial.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                eq('company', company)
                order("name", "asc")
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
                order("name", "asc")
            }
        }
        return rawMaterialList
    }

    List<Cliente> getRawMaterialByCompany(Company company){
        List<RawMaterial> rawMaterialList = []
        Cliente.withTransaction {
            rawMaterialList = RawMaterial.createCriteria().list {
                eq('company', company)
                order("name", "asc")
            }
        }
        return rawMaterialList
    }

    public ErrorUi validationRawMaterial(RawMaterial rawMaterial){
        ErrorUi errorUi = new ErrorUi()

        if(!rawMaterial.name){
            errorUi.addError('name', 'Por favor preencha o nome da materia prima')
        }

        if(!rawMaterial.price){
            errorUi.addError('price', 'Por favor preencha o preço da materia prima')
        }

        return errorUi
    }

    Boolean changesPriceRawMaterialProduct(RawMaterial rawMaterial, User user, Company company) {
        try {
            List<ProductRawMaterial> productRawMaterialList = ProductRawMaterial.createCriteria().list {
                eq('rawMaterial', rawMaterial)
                eq('company', company)
                fetchMode("product", FetchMode.JOIN)
                fetchMode("company", FetchMode.JOIN)
                fetchMode("rawMaterial", FetchMode.JOIN)
            }

            if (productRawMaterialList.size() > 0) {
                productRawMaterialList.each { rm ->
                    calculationProductRawMaterial(rm.product, company, user)
                }
            }

            return true
        } catch (Exception e){
            e.printStackTrace()
            return false
        }

    }

    // Vou usar para atualizar os preços
    void calculationProductRawMaterial(Product product, Company company, User user){
        List<ProductRawMaterial> productRawMaterialList = ProductRawMaterial.createCriteria().list {
            eq('product', product)
            eq('company', company)
            fetchMode("product", FetchMode.JOIN)
            fetchMode("company", FetchMode.JOIN)
            fetchMode("rawMaterial", FetchMode.JOIN)
        }

        if(productRawMaterialList.size > 0){
            Double productOrderPrice = 0

            productRawMaterialList.each { productRawMaterial ->
                if(user) productRawMaterial.updatedBy = user.username
                productRawMaterial.dateUpdated = new Date()
                productRawMaterial.product = product
                productRawMaterial.calculationPrice(productRawMaterial.rawMaterial)
                productRawMaterial.save(flush:true)

                productOrderPrice += productRawMaterial.price
            }

            product.updatedBy = user.username
            product.dateUpdated = new Date()
            product.productOrderPrice = productOrderPrice
            product.calculationPriceSales(product.productOrderPrice)
            product.calculationWholesalePrice(product.productOrderPrice)
            product.save(flush:true)
        }
    }

    public Boolean save(RawMaterial rawMaterial, Company company, String username) {
        rawMaterial.dateCreated = new Date()
        rawMaterial.dateUpdated = new Date()

        rawMaterial.createdBy = username
        rawMaterial.updatedBy = username

        rawMaterial.company = company

        RawMaterial.withTransaction {
            try{
                rawMaterial.save(flush:true)
            } catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }
        return true
    }
}
