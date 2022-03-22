package br.com.doistech.utils

class ErrorUi {

    Boolean hasError = false
    List<Map> errorUiList = []

    public void addError(String codeError, String errorMessage){
        errorUiList.add([codeError: codeError, errorMessage: ' - ' + errorMessage])
        hasError = true
    }

    public void clearListError(){
        errorUiList = []
    }
}
