package br.com.doistech.viewmodels

import br.com.doistech.domain.Company
import br.com.doistech.domain.Product
import br.com.doistech.domain.ProductRawMaterial
import br.com.doistech.domain.RawMaterial
import br.com.doistech.domain.User
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.ProductRawMaterialService
import br.com.doistech.service.ProductService
import br.com.doistech.service.RawMaterialService
import br.com.doistech.utils.ErrorUi
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class ProductVM {
    ProductService productService
    RawMaterialService rawMaterialService
    ProductRawMaterialService productRawMaterialService

    List<Product> productList = []
    List<RawMaterial> rawMaterialList = []
    List<ProductRawMaterial> productRawMaterialList = []
    List<ProductRawMaterial> productRawMaterialAuxList = []

    public static final List<Map> UNIT_MEASUREMENT_LIST = [
            [initials: 'UN', name: 'Unidade'],
            [initials: 'G', name: 'Grama'],
            [initials: 'KG', name: 'Quilograma'],
            [initials: 'PÇ', name: 'Peça'],
            [initials: 'PC', name: 'Pacote'],
            [initials: 'M', name: 'Metro'],
            [initials: 'CM', name: 'Centímetro']
    ]

    Session session
    ErrorUi errorUi

    User user
    Company company
    Product product
    RawMaterial rawMaterial = new RawMaterial()
    ProductRawMaterial productRawMaterial = new ProductRawMaterial()

    Boolean visibleForm = false

    String searchName
    String actionForm

    @Init
    void init(){
        productService = InjectUtils.getBean('productService')
        rawMaterialService = InjectUtils.getBean('rawMaterialService')
        productRawMaterialService = InjectUtils.getBean('productRawMaterialService')

        session = Sessions.getCurrent()
        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        product = new Product()
        productList = productService.getProductByCompany(company)
        rawMaterialList = rawMaterialService.getRawMaterialByCompany(company)
    }

    @Command
    @NotifyChange(['*'])
    void newProduct() {
        product = new Product()
        visibleForm = true
        actionForm = 'new'
    }

    @Command
    @NotifyChange(['*'])
    void searchProduct(){
        productList = productService.searchProduct(searchName, company)
    }

    @Command
    @NotifyChange(['*'])
    void selectProductGrid(@BindingParam("productSelect") Product productSelect){
        product = productSelect
        //Precisamos saber se tem lista
        productRawMaterialList = productRawMaterialService.getProductRawMaterialAndProductList(company, product)
        actionForm = 'edit'
        visibleForm = true
    }

    @Command
    @NotifyChange(['*'])
    void returnListProduct() {
        product = new Product()
        productList = productService.getProductByCompany(company)
        visibleForm = false
    }

    @Command
    @NotifyChange(['*'])
    void delete(){
        if(product){
            product.isDelete = true
            product.dateDelete = new Date()
            product.deleteBy = user.username

            Boolean saveError = false
            saveError = productService.save(product, company, user.username)

            if(saveError){
                returnListProduct()
            } else {
                Messagebox.show("Erro na exclusão do Produto. Tente mais tarde!",
                        'Simplifica MEI', 0,  Messagebox.ERROR)
            }
        }
    }

    @Command
    @NotifyChange(['*'])
    void save(){
        errorUi = productService.validationProduct(product)

        Boolean saveError = false
        if(!errorUi.hasError){
            saveError = productService.save(product, company, user.username)

            productRawMaterialList.each { ProductRawMaterial prm ->
                saveError = productRawMaterialService.save(prm, company, user.username)
            }

            if(!saveError){
                Messagebox.show("Erro na inclusão da Materia Prima. Tente mais tarde!",
                        'Simplifica MEI', 0,  Messagebox.ERROR)
                visibleForm = false
            } else {
//                visibleForm = true
//                product = new Product()
                returnListProduct()
            }
        }
    }

    @Command
    @NotifyChange(['*'])
    void addProductRawMaterial(){
        try{
            if(!productRawMaterial.quantity){
                Messagebox.show("Por favor insira a quantidade de insumo!",
                        'Simplifica MEI', 0,  Messagebox.ERROR)
            } else {
                productRawMaterial.company = company
                productRawMaterial.createdBy = user.username
                productRawMaterial.updatedBy = user.username
                productRawMaterial.rawMaterial = rawMaterial
                productRawMaterial.product = product
                productRawMaterial.calculationPrice(rawMaterial)

                productRawMaterialList.add(productRawMaterial)
                calculationProductOrderPrice()
    //            product.calculationSalesOrderPrice(productRawMaterialList)
    //            calculationPriceSales()
                productRawMaterial = new ProductRawMaterial()
            }
        }catch(Exception e){
            e.printStackTrace()
        }
    }

    @Command
    @NotifyChange(['*'])
    void deletItemProductRawMaterial(@BindingParam("prm") ProductRawMaterial prm){
        try{
            if(prm.id != null){
                productRawMaterialList.remove(prm)
                ProductRawMaterial.withTransaction {
                    prm.delete(flush:true)
                }
            } else {
                productRawMaterialList.remove(prm)
            }
            calculationProductOrderPrice()
        }catch(Exception e){
            e.printStackTrace()
        }
//        calculationPriceSales()
    }

    @NotifyChange(['*'])
    void calculationProductOrderPrice(){
        try{
            product.productOrderPrice = 0
            productRawMaterialList.each { pmp ->
                product.productOrderPrice += pmp.price
            }
            calculationPriceSales()
        }catch(Exception e){
            e.printStackTrace()
        }
    }

    @Command
    @NotifyChange(['*'])
    void calculationPriceSales(){
        try{
            if(!product.hasPriceMethod){
                product.calculationPriceSales(product.productOrderPrice)
                product.calculationWholesalePrice(product.productOrderPrice)
            } else {
                product.calculationPriceSales(product.productOrderPrice)
                product.calculationWholesalePrice(product.productOrderPrice)
            }
        }catch(Exception e){
            e.printStackTrace()
        }
    }


}
