/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service;

import deu.model.dto.request.data.user.*;
import deu.model.dto.response.BasicResponse;
/**
 *
 * @author limmi
 */
public class UserService {
    
    private static final UserService instance = new UserService();
    
    private UserService() {}
    public static UserService getInstance() {return instance;}
    
    public BasicResponse find(FindRequest payload) {
        throw new UnsupportedOperationException("Client: find() logic execution is not supported");
    }
    public BasicResponse update(UserDataModificationRequest payload) {
        throw new UnsupportedOperationException("Client: update() logic execution is not supported");
    }
    public BasicResponse delete(DeleteRequest payload) {
        throw new UnsupportedOperationException("Client: delete() logic execution is not supported");
    }
    public BasicResponse findAll() {
        throw new UnsupportedOperationException("Client: findAll() logic execution is not supported");
    }
}
