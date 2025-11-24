package deu.controller.business;

import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;

/**
 * ⭐⭐⭐ Singleton 패턴 구현 - AccountManagerController ⭐⭐⭐
 * YAML 파일(users.yaml)을 사용하도록 수정됨
 */
public class AccountManagerController {
    
    private static final AccountManagerController instance = new AccountManagerController();
    private final UserService userService = UserService.getInstance(); // AccountManager 대신 UserService 사용
    private final UserController userController = UserController.getInstance();
    
    private AccountManagerController() {
        System.out.println("[AccountManagerController] ⭐ Singleton 인스턴스 생성 완료 (YAML 사용)");
    }
    
    public static AccountManagerController getInstance() {
        return instance;
    }
    
    /**
     * YAML 기반 로그인 처리
     */
    public Object handleLogin(LoginRequest payload) {
        BasicResponse response = userService.login(payload);
        
        // 로그인 성공 시 UserController의 userNumbers에 추가
        if (response.code.equals("200")) {
            userController.addUserNumber(payload.number);
            System.out.println("[AccountManagerController] ⭐ YAML 기반 로그인 성공 - userNumbers에 추가: " + payload.number);
        }
        
        return response;
    }
    
    /**
     * YAML 기반 회원가입 처리
     */
    public Object handleSignup(SignupRequest payload) {
        System.out.println("[AccountManagerController] YAML 기반 회원가입 호출: number=" + payload.number + ", name=" + payload.name);
        try {
            // ID 규칙 검증 (s: 학생, p: 교수, m: 관리자)
            String firstChar = payload.number.toLowerCase().substring(0, 1);
            if (!firstChar.equals("s") && !firstChar.equals("p") && !firstChar.equals("m")) {
                return new BasicResponse("400", "ID는 s(학생), p(교수), m(관리자)로 시작해야 합니다.");
            }
            
            BasicResponse response = userService.signup(payload);
            System.out.println("[AccountManagerController] YAML 기반 회원가입 처리 완료: code=" + response.code + ", data=" + response.data);
            return response;
        } catch (Exception e) {
            System.err.println("[AccountManagerController] 회원가입 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new BasicResponse("500", "회원가입 처리 중 오류 발생: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 역할 조회 (ID 첫 글자 기반)
     */
    public Object handleGetRole(String number) {
        if (number == null || number.isEmpty()) {
            return new BasicResponse("400", "사용자 번호가 없습니다.");
        }
        
        String firstChar = number.toLowerCase().substring(0, 1);
        String role;
        switch (firstChar) {
            case "s":
                role = "Student";
                break;
            case "p":
                role = "Professor";
                break;
            case "m":
                role = "Admin";
                break;
            default:
                return new BasicResponse("400", "잘못된 사용자 번호 형식입니다.");
        }
        
        return new BasicResponse("200", role);
    }
    
    /**
     * 로그아웃 처리
     */
    public Object handleLogout(LogoutRequest payload) {
        return userController.handleLogout(payload);
    }
}