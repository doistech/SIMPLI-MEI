package br.com.doistech.domain

import java.security.MessageDigest

class User {
    String createdBy = "SIMEI-DEFAULT"
    String updatedBy = "SIMEI-DEFAULT"

    Date dateCreated = new Date()
    Date dateUpdated = new Date()

    String name
    String username
    String password
    String passwordConfirmation


    static constraints = {
        passwordConfirmation nullable: true
    }

    private void encryptPassword(){
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        digest.update(password.getBytes("ASCII")) //mudar para "UTF-8" se for preciso

        byte[] passwordDigest = digest.digest()

        String hexString = passwordDigest.collect { String.format('%02x', it) }.join()

        this.password = hexString
        this.passwordConfirmation = hexString
    }
}
