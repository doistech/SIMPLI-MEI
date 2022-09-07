package br.com.doistech.service

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.Product
import br.com.doistech.domain.RawMaterial
import br.com.doistech.utils.ErrorUi
import grails.transaction.Transactional

@Transactional
class ProductService {

    List<Product> getProductList(Company company){
        List<Product> productList = []

        Product.withTransaction {
            productList = RawMaterial.createCriteria().list {
                eq('company', company)
                eq('isDelete', false)
            }
        }

        return productList
    }

    List<RawMaterial> searchProduct(String name, Company company){
        List<Product> productList = []

        Product.withTransaction {
            productList = Product.createCriteria().list {
                like('name', '%' + name + '%')
                eq('company', company)
                eq('isDelete', false)
            }
        }
        return productList
    }

    List<Cliente> getProductByCompany(Company company){
        List<Product> productList = []

        Product.withTransaction {
            productList = Product.createCriteria().list {
                eq('company', company)
                eq('isDelete', false)
                order("name", "asc")
            }
        }
        return productList
    }

    public ErrorUi validationProduct(Product product){
        ErrorUi errorUi = new ErrorUi()

        if(!product.name){
            errorUi.addError('name', 'Por favor preencha o nome do produto')
        }

        if(!product.salesPrice){
            errorUi.addError('price', 'Por favor preencha a % Lucro Varejo')
        }
        return errorUi
    }

    public Boolean save(Product product, Company company, String username) {
        product.dateCreated = new Date()
        product.dateUpdated = new Date()

        product.createdBy = username
        product.updatedBy = username

        product.company = company
        product.codeEan = ''
        product.description = ''

        Product.withTransaction {
            try{
                product.save(flush:true)
            } catch(Exception e){
                println('Error - ' + e.message)
                return false
            }
        }
        return true
    }
}
