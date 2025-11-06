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
import java.util.*;
import java.io.*;

public class UserController {
    private final String usersFilePath = "users.txt"; // 사용자 정보가 저장될 파일 경로
    private List<User> users; // 메모리 내 사용자 정보

    public UserController() {
        users = new ArrayList<>();
        loadUsersFromFile(); // 시스템 시작 시 사용자 파일 로드
    }

    // 사용자 정보를 파일에서 로드
    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) { // username,password 형식
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 정보를 불러오는 중 오류: " + e.getMessage());
        }
    }

    // 로그인 기능
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                UserSession.getInstance().login(user); // 로그인 성공 시 세션에 저장
                return true;
            }
        }
        return false; // 로그인 실패
    }

    // 로그아웃 기능
    public void logout() {
        UserSession.getInstance().logout(); // 세션 초기화
    }

    // 회원가입 기능
    public boolean register(String username, String password) {
        // 아이디 중복 체크
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // 중복된 사용자 이름
            }
        }

        // 새로운 사용자 추가
        User newUser = new User(username, password);
        users.add(newUser);

        // 정보를 파일에 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, true))) {
            writer.write(username + "," + password);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("회원가입 정보 저장 중 오류 발생: " + e.getMessage());
            return false; // 파일 저장 실패
        }

        return true; // 회원가입 성공
    }
}
