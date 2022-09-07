package br.com.doistech.service

import java.security.*
import br.com.doistech.domain.User
import grails.transaction.Transactional

@Transactional
class UserProfileService {

    SessionService sessionService

    public User getUser(String email){
        User user

        User.withTransaction {
            user = User.createCriteria().get {
                eq('username', email)
            }
        }

        return user
    }

    public String encryptPassword(String password){
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        digest.update(password.getBytes("ASCII")) //mudar para "UTF-8" se for preciso
        byte[] passwordDigest = digest.digest()
        String hexString = passwordDigest.collect { String.format('%02x', it) }.join()
        return hexString
    }



}
