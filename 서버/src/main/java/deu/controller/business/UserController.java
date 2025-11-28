package deu.controller.business;

import deu.model.dto.request.data.user.FindUserNameRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.response.CurrentResponse;
import deu.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    private static final UserController instance = new UserController();

    private UserController() {}

    public static UserController getInstance() {
        return instance;
    }

    private final UserService userService = UserService.getInstance();
    private final List<String> userNumbers = new ArrayList<>();

    // [일반 로그인] 대기열 적용
    public synchronized Object handleLogin(LoginRequest payload) {
        if (userNumbers.contains(payload.number)) {
            return new BasicResponse("400", "이미 로그인된 사용자입니다.");
        }

        // 3명이 꽉 찼으면 자리가 날 때까지 대기
        while (userNumbers.size() >= 3) {
            try {
                System.out.println("⏳ [대기 진입] 현재 인원 만원. " + payload.number + "님 대기 중...");
                wait(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new BasicResponse("500", "서버 대기 중 오류 발생");
            }
        }

        BasicResponse result = userService.login(payload);
        if ((result.code).equals("200")) {
            userNumbers.add(payload.number);
            System.out.println("✅ [입장] " + payload.number + "님 로그인 성공! (현재 접속자: " + userNumbers.size() + "명)");
        }

        return result;
    }

    public Object handleSignup(SignupRequest payload) {
        return userService.signup(payload);
    }

    // [로그아웃] 대기자 깨우기 (notifyAll)
    public synchronized Object handleLogout(LogoutRequest payload) {
        if(userNumbers.remove(payload.number)){
            System.out.println("🚪 [퇴장] " + payload.number + "님 로그아웃. (현재 접속자: " + userNumbers.size() + "명)");
            notifyAll(); // 대기 중인 사람 깨움
            return new BasicResponse("200", "로그아웃 성공");
        } else {
            return new BasicResponse("200", "이미 로그아웃 되었습니다.");
        }
    }

    public CurrentResponse handleCurrentUser(){
        return new CurrentResponse(userNumbers.size());
    }

    public Object handleFindUserName(FindUserNameRequest payload) {
        return userService.findUserName(payload);
    }
    
    // [AccountManager용] boolean 반환으로 수정됨
    public synchronized boolean addUserNumber(String number) {
        if (userNumbers.contains(number)) return true;

        while (userNumbers.size() >= 3) {
            try {
                System.out.println("⏳ [AccountManager 대기] " + number + " 대기 중...");
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }

        userNumbers.add(number);
        System.out.println("✅ [AccountManager 입장] " + number + "님 추가됨. (현재 접속자: " + userNumbers.size() + "명)");
        return true;
    }
}