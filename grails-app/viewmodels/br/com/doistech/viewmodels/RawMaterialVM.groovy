package br.com.doistech.viewmodels

import br.com.doistech.domain.Cliente
import br.com.doistech.domain.Company
import br.com.doistech.domain.RawMaterial
import br.com.doistech.domain.User
import br.com.doistech.service.ClientService
import br.com.doistech.service.InjectUtils
import br.com.doistech.service.RawMaterialService
import br.com.doistech.utils.ErrorUi
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.Sessions

class RawMaterialVM {

    RawMaterialService rawMaterialService

    Session session

    ErrorUi errorUi

    User user
    Company company

    RawMaterial rawMaterial
    List<RawMaterial> rawMaterialList = []
    List<String> materialTypeList = ['Insumo', 'Serviços']

    Boolean visibleForm = false

    String searchName
    String materialType


    @Init
    void init(){
        rawMaterialService = InjectUtils.getBean('rawMaterialService')

        session = Sessions.getCurrent()
        user = (User) session.getAttribute('user')
        company = (Company) session.getAttribute('company')

        rawMaterial = new RawMaterial()

        rawMaterialList = rawMaterialService.getRawMaterialList(company)
    }

    @Command
    @NotifyChange(['*'])
    void newRawMaterial() {
        rawMaterial = new RawMaterial()
        visibleForm = true
    }

    @Command
    @NotifyChange(['*'])
    void searchRawMaterial(){
        rawMaterialList = rawMaterialService.searchRawMaterial(searchName, company)
    }

    @Command
    @NotifyChange(['*'])
    void selectRawMaterialGrid(@BindingParam("rawMaterialSelect") RawMaterial rawMaterialSelect){
        rawMaterial = rawMaterialSelect
        visibleForm = true
    }

    @Command
    @NotifyChange(['*'])
    void returnListRawMaterial() {
        rawMaterial = new RawMaterial()
        rawMaterialList = rawMaterialService.getRawMaterialByCompany(company)
        visibleForm = false
    }

    @Command
    @NotifyChange(['*'])
    void save(){
        errorUi = rawMaterialService.validationRawMaterial(rawMaterial)

        Boolean saveError = false
        if(!errorUi.hasError){
            rawMaterial.materialType = materialType
            saveError = rawMaterialService.save(rawMaterial, company, user.username)

            if(!saveError){
                Messagebox.show("Erro na inclusão da Materia Prima. Tente mais tarde!",
                        'Simplifica MEI', 0,  Messagebox.ERROR)
                visibleForm = false
            } else {
                Boolean resultUpdated = rawMaterialService.changesPriceRawMaterialProduct(rawMaterial, user, company)
                visibleForm = true
                rawMaterial = new RawMaterial()
                materialType = ''
            }
        }
    }

}
