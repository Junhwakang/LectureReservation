package deu.service;

import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.entity.User;

import java.io.*;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ⭐⭐⭐ Singleton 패턴 구현 - AccountManager ⭐⭐⭐
 * 
 * 이 클래스는 Singleton 패턴을 사용하여 구현되었습니다.
 * 
 * Singleton 패턴의 특징:
 * 1. private 생성자로 외부에서 직접 인스턴스 생성 방지
 * 2. static getInstance() 메서드를 통한 단일 인스턴스 반환
 * 3. synchronized 키워드로 멀티스레드 환경에서의 안전성 보장
 * 
 * Singleton 패턴을 사용하는 이유:
 * - 전역에서 단일 인스턴스만 존재하도록 보장
 * - 사용자 데이터를 메모리에 캐시하여 성능 향상
 * - 여러 컨트롤러에서 동일한 AccountManager 인스턴스를 공유
 * 
 * @author AccountManager Team
 * @version 1.0
 */
public class AccountManager {
    
    // ⭐ Singleton 패턴: private static 인스턴스 변수
    private static AccountManager instance;
    
    // 메모리 내 사용자 리스트 (캐시)
    private final List<User> users;
    
    // 사용자 역할 정보 (number -> role 매핑)
    private final Map<String, String> userRoles;
    
    // 파일 경로
    private static final String FILE_NAME = "users.txt";
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = Paths.get(DATA_DIR, FILE_NAME).toString();
    
    /**
     * ⭐ Singleton 패턴: private 생성자
     * 외부에서 new AccountManager()로 인스턴스를 생성할 수 없도록 함
     */
    private AccountManager() {
        this.users = new ArrayList<>();
        this.userRoles = new HashMap<>();
        loadUsersFromFile(); // 시작 시 파일에서 사용자 정보 로드
        System.out.println("[AccountManager] ⭐ Singleton 인스턴스 생성 완료");
    }
    
    /**
     * ⭐ Singleton 패턴: getInstance() 메서드
     * 
     * Thread-safe Lazy Initialization 방식:
     * - synchronized 키워드로 멀티스레드 환경에서도 안전
     * - 최초 호출 시에만 인스턴스 생성 (Lazy)
     * - 이후 호출 시에는 기존 인스턴스 반환
     * 
     * @return AccountManager의 유일한 인스턴스
     */
    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    /**
     * 파일에서 사용자 정보 로드
     * 형식: number|password|name|major|role
     */
    private void loadUsersFromFile() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        
        // 디렉토리 생성
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 파일이 없으면 빈 리스트로 시작
        if (!file.exists()) {
            System.out.println("[AccountManager] users.txt 파일이 없습니다. 새로 생성합니다.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            users.clear();
            userRoles.clear();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // 빈 줄이나 주석 건너뛰기
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String number = parts[0];
                    String password = parts[1]; // 암호화된 비밀번호
                    String name = parts[2];
                    String major = parts[3];
                    String role = parts[4];
                    
                    // User 객체 생성
                    User user = new User(number, password, name, major);
                    users.add(user);
                    userRoles.put(number, role);
                }
            }
            
            System.out.println("[AccountManager] ⭐ Singleton 인스턴스에서 " + users.size() + "명의 사용자를 로드했습니다.");
        } catch (IOException e) {
            System.err.println("[AccountManager] 파일 로드 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 정보를 파일에 저장
     * 형식: number|password|name|major|role
     */
    private void saveUsersToFile() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        
        // 디렉토리 생성
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User user : users) {
                String role = userRoles.getOrDefault(user.getNumber(), "Student");
                
                writer.write(String.format("%s|%s|%s|%s|%s",
                    user.getNumber(),
                    user.getPassword(), // 암호화된 비밀번호
                    user.getName(),
                    user.getMajor(),
                    role));
                writer.newLine();
            }
            System.out.println("[AccountManager] ⭐ Singleton 인스턴스에서 " + users.size() + "명의 사용자 정보를 저장했습니다.");
        } catch (IOException e) {
            System.err.println("[AccountManager] 파일 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 비밀번호 암호화 (SHA-256)
     */
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("[AccountManager] 암호화 알고리즘 오류: " + e.getMessage());
            return password; // 암호화 실패 시 원본 반환
        }
    }
    
    /**
     * ID 규칙 검증 및 역할 반환
     * s로 시작: Student
     * p로 시작: Professor
     * m으로 시작: Admin
     */
    private String validateIdAndGetRole(String number) {
        if (number == null || number.isEmpty()) {
            return null;
        }
        
        char firstChar = number.charAt(0);
        if (firstChar == 's' || firstChar == 'S') {
            return "Student";
        } else if (firstChar == 'p' || firstChar == 'P') {
            return "Professor";
        } else if (firstChar == 'm' || firstChar == 'M') {
            return "Admin";
        }
        
        return null; // 규칙에 맞지 않음
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 회원가입 처리
     * 
     * @param payload 회원가입 요청 데이터
     * @return BasicResponse 응답
     */
    public BasicResponse signup(SignupRequest payload) {
        try {
            // ID 규칙 검증
            String role = validateIdAndGetRole(payload.number);
            if (role == null) {
                return new BasicResponse("400", "ID는 s(학생), p(교수), m(관리자)로 시작해야 합니다.");
            }
            
            // 중복 ID 확인
            for (User user : users) {
                if (user.getNumber().equals(payload.number)) {
                    return new BasicResponse("400", "이미 가입된 사용자 정보입니다.");
                }
            }
            
            // 비밀번호 암호화
            String encryptedPassword = encryptPassword(payload.password);
            
            // 사용자 생성 및 추가
            User newUser = new User(payload.number, encryptedPassword, payload.name, payload.major);
            users.add(newUser);
            userRoles.put(payload.number, role);
            
            saveUsersToFile(); // 파일에 저장
            
            System.out.println("[AccountManager] ⭐ Singleton 인스턴스를 통한 회원가입 성공: " + payload.number);
            return new BasicResponse("200", "회원가입 성공");
        } catch (Exception e) {
            System.err.println("[AccountManager] 회원가입 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return new BasicResponse("500", "회원가입 실패");
        }
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 로그인 처리
     * 
     * @param payload 로그인 요청 데이터
     * @return BasicResponse 응답 (성공 시 User 객체 포함)
     */
    public BasicResponse login(LoginRequest payload) {
        try {
            // 사용자 찾기
            User foundUser = null;
            for (User user : users) {
                if (user.getNumber().equals(payload.number)) {
                    foundUser = user;
                    break;
                }
            }
            
            // 사용자가 존재하지 않음
            if (foundUser == null) {
                return new BasicResponse("404", "존재하지 않는 아이디입니다.");
            }
            
            // 비밀번호 확인 (암호화된 비밀번호와 비교)
            String encryptedInputPassword = encryptPassword(payload.password);
            if (!foundUser.getPassword().equals(encryptedInputPassword)) {
                return new BasicResponse("401", "비밀번호 입력 오류입니다.");
            }
            
            // 로그인 성공 - User 객체 반환
            System.out.println("[AccountManager] ⭐ Singleton 인스턴스를 통한 로그인 성공: " + payload.number);
            return new BasicResponse("200", foundUser);
        } catch (Exception e) {
            System.err.println("[AccountManager] 로그인 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return new BasicResponse("500", "로그인 처리 중 시스템 오류가 발생했습니다.");
        }
    }
    
    /**
     * 사용자 역할 조회
     * 
     * @param number 사용자 번호
     * @return 역할 (Student, Professor, Admin)
     */
    public String getUserRole(String number) {
        return userRoles.get(number);
    }
}

