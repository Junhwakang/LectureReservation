/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author jun
 */
import Model.User;

public class UserSession {
    private static UserSession instance; // Singleton 패턴으로 관리
    private User loggedInUser; // 현재 로그인된 사용자

    private UserSession() {
    }

    // 싱글톤 인스턴스 반환
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // 현재 로그인된 사용자 반환
    public User getLoggedInUser() {
        return loggedInUser;
    }

    // 로그인 처리
    public void login(User user) {
        this.loggedInUser = user;
    }

    // 로그아웃 처리
    public void logout() {
        this.loggedInUser = null;
    }

    // 로그인 여부 확인
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
