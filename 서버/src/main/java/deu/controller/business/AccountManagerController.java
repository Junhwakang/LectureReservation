package deu.controller.business;

import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;

public class AccountManagerController {
    
    private static final AccountManagerController instance = new AccountManagerController();
    private final UserService userService = UserService.getInstance();
    private final UserController userController = UserController.getInstance();
    
    private AccountManagerController() {}
    
    public static AccountManagerController getInstance() {
        return instance;
    }
    
    public Object handleLogin(LoginRequest payload) {
        BasicResponse response = userService.login(payload);
        
        if (response.code.equals("200")) {
            // boolean 결과 확인
            boolean added = userController.addUserNumber(payload.number);
            if (!added) {
                return new BasicResponse("403", "접속 인원 초과");
            }
        }
        return response;
    }
    
    public Object handleSignup(SignupRequest payload) {
        // 기존 코드 유지 (생략)
        try {
             return userService.signup(payload);
        } catch (Exception e) { return new BasicResponse("500", "오류"); }
    }
    
    public Object handleGetRole(String number) {
        // 기존 코드 유지 (생략)
        if (number.startsWith("m")) return new BasicResponse("200", "Admin");
        return new BasicResponse("200", "Student");
    }
    
    public Object handleLogout(LogoutRequest payload) {
        return userController.handleLogout(payload);
    }
}