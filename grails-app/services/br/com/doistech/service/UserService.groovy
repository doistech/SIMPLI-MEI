package br.com.doistech.service

import br.com.doistech.domain.User
import grails.transaction.Transactional

@Transactional
class UserService {

    public User getUser(String email){
        User user

        User.withTransaction {
            user = User.createCriteria().get {
                eq('username', email)
            }
        }

        return user
    }
}
