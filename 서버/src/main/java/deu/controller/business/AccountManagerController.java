package deu.controller.business;

import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.AccountManager;

/**
 * ⭐⭐⭐ Singleton 패턴 구현 - AccountManagerController ⭐⭐⭐
 * 
 * 이 컨트롤러는 Singleton 패턴을 사용하여 구현되었습니다.
 * 
 * Singleton 패턴 적용:
 * - Eager Initialization 방식 사용
 * - private static final instance로 클래스 로딩 시 인스턴스 생성
 * - private 생성자로 외부에서 직접 인스턴스 생성 방지
 * 
 * AccountManager와의 관계:
 * - AccountManager.getInstance()를 통해 Singleton 인스턴스 사용
 * - 여러 컨트롤러에서 동일한 AccountManager 인스턴스를 공유
 * 
 * @author AccountManager Team
 * @version 1.0
 */
public class AccountManagerController {
    
    // ⭐ Singleton 패턴: Eager Initialization 방식
    // 클래스가 로딩될 때 즉시 인스턴스 생성
    private static final AccountManagerController instance = new AccountManagerController();
    
    // ⭐ Singleton 패턴: AccountManager의 Singleton 인스턴스 사용
    private final AccountManager accountManager = AccountManager.getInstance();
    
    private final UserController userController = UserController.getInstance(); // 로그아웃을 위해 필요
    
    /**
     * ⭐ Singleton 패턴: private 생성자
     * 외부에서 new AccountManagerController()로 인스턴스를 생성할 수 없도록 함
     */
    private AccountManagerController() {
        System.out.println("[AccountManagerController] ⭐ Singleton 인스턴스 생성 완료");
    }
    
    /**
     * ⭐ Singleton 패턴: getInstance() 메서드
     * 
     * Eager Initialization 방식:
     * - 클래스 로딩 시 인스턴스가 이미 생성되어 있음
     * - 멀티스레드 환경에서도 안전 (JVM이 클래스 로딩을 동기화)
     * 
     * @return AccountManagerController의 유일한 인스턴스
     */
    public static AccountManagerController getInstance() {
        return instance;
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 로그인 처리
     * AccountManager의 Singleton 인스턴스를 통해 로그인 처리
     * 
     * @param payload 로그인 요청 데이터
     * @return BasicResponse 응답
     */
    public Object handleLogin(LoginRequest payload) {
        BasicResponse response = accountManager.login(payload);
        
        // 로그인 성공 시 UserController의 userNumbers에 추가
        if (response.code.equals("200")) {
            userController.addUserNumber(payload.number);
            System.out.println("[AccountManagerController] ⭐ Singleton 인스턴스를 통한 로그인 성공 - userNumbers에 추가: " + payload.number);
        }
        
        return response;
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 회원가입 처리
     * AccountManager의 Singleton 인스턴스를 통해 회원가입 처리
     * 
     * @param payload 회원가입 요청 데이터
     * @return BasicResponse 응답
     */
    public Object handleSignup(SignupRequest payload) {
        return accountManager.signup(payload);
    }
    
    /**
     * 사용자 역할 조회
     * AccountManager의 Singleton 인스턴스를 통해 역할 조회
     * 
     * @param number 사용자 번호
     * @return BasicResponse 응답
     */
    public Object handleGetRole(String number) {
        String role = accountManager.getUserRole(number);
        if (role != null) {
            return new BasicResponse("200", role);
        } else {
            return new BasicResponse("404", "사용자를 찾을 수 없습니다.");
        }
    }
    
    /**
     * AccountManager를 사용한 로그아웃 처리
     * UserController의 로그아웃을 사용하여 처리
     * 
     * @param payload 로그아웃 요청 데이터
     * @return BasicResponse 응답
     */
    public Object handleLogout(LogoutRequest payload) {
        // UserController의 로그아웃을 사용 (userNumbers에서 제거)
        return userController.handleLogout(payload);
    }
}

