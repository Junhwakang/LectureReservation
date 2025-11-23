package deu.controller.business;

import deu.config.Config;
import deu.config.ConfigLoader;
import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.request.command.UserCommandRequest;
import deu.model.dto.response.BasicResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * ⭐⭐⭐ Singleton 패턴 구현 - AccountManagerClientController ⭐⭐⭐
 * 
 * 이 클라이언트 컨트롤러는 Singleton 패턴을 사용하여 구현되었습니다.
 * 
 * Singleton 패턴의 특징:
 * 1. private 생성자로 외부에서 직접 인스턴스 생성 방지
 * 2. static synchronized getInstance() 메서드를 통한 단일 인스턴스 반환
 * 3. Thread-safe Lazy Initialization 방식
 * 
 * Singleton 패턴을 사용하는 이유:
 * - 클라이언트 전체에서 단일 인스턴스만 사용
 * - 서버 연결 설정을 한 곳에서 관리
 * - 메모리 효율성 향상
 * 
 * @author AccountManager Team
 * @version 1.0
 */
public class AccountManagerClientController {
    
    // ⭐ Singleton 패턴: private static 인스턴스 변수
    private static AccountManagerClientController instance;
    
    // 설정파일 불러오기
    private final Config config = ConfigLoader.getConfig();
    private final String host = config.server.host;
    private final int port = config.server.port;
    
    /**
     * ⭐ Singleton 패턴: private 생성자
     * 외부에서 new AccountManagerClientController()로 인스턴스를 생성할 수 없도록 함
     */
    private AccountManagerClientController() {
        System.out.println("[AccountManagerClientController] ⭐ Singleton 인스턴스 생성 완료");
    }
    
    /**
     * ⭐ Singleton 패턴: getInstance() 메서드
     * 
     * Thread-safe Lazy Initialization 방식:
     * - synchronized 키워드로 멀티스레드 환경에서도 안전
     * - 최초 호출 시에만 인스턴스 생성 (Lazy)
     * - 이후 호출 시에는 기존 인스턴스 반환
     * 
     * @return AccountManagerClientController의 유일한 인스턴스
     */
    public static synchronized AccountManagerClientController getInstance() {
        if (instance == null) {
            instance = new AccountManagerClientController();
        }
        return instance;
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 로그인 요청
     * 서버의 AccountManager.login()을 호출합니다.
     * 
     * @param number 사용자 번호
     * @param password 비밀번호
     * @return BasicResponse 응답
     */
    public BasicResponse login(String number, String password) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            LoginRequest loginRequest = new LoginRequest(number, password);
            // "AccountManager로그인" 명령으로 서버에 전송
            UserCommandRequest req = new UserCommandRequest("AccountManager로그인", loginRequest);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                System.out.println("[AccountManagerClient] ⭐ Singleton 인스턴스를 통한 서버 응답: " + r.code + " - " + r.data);
                
                if (r.code.equals("200")) {
                    return r; // 로그인 성공
                } else if (r.code.equals("401")) {
                    return r; // 비밀번호 오류
                } else if (r.code.equals("404")) {
                    return r; // 존재하지 않는 사용자
                } else {
                    return r; // 기타 오류
                }
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 회원가입 요청
     * 서버의 AccountManager.signup()을 호출합니다.
     * 
     * ID 규칙:
     * - s로 시작: Student (학생)
     * - p로 시작: Professor (교수)
     * - m으로 시작: Admin (관리자)
     * 
     * @param number 사용자 번호
     * @param password 비밀번호
     * @param name 이름
     * @param major 전공
     * @return BasicResponse 응답
     */
    public BasicResponse signup(String number, String password, String name, String major) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            SignupRequest signupRequest = new SignupRequest(number, password, name, major);
            // "AccountManager회원가입" 명령으로 서버에 전송
            UserCommandRequest req = new UserCommandRequest("AccountManager회원가입", signupRequest);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                System.out.println("[AccountManagerClient] ⭐ Singleton 인스턴스를 통한 서버 응답: " + r.code + " - " + r.data);
                
                if (r.code.equals("200")) {
                    return r; // 회원가입 성공
                } else {
                    return r; // 회원가입 실패 (ID 규칙 위반, 중복 등)
                }
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * ⭐ Singleton 패턴을 사용한 로그아웃 요청
     * 
     * @param number 사용자 번호
     * @param password 비밀번호
     * @return BasicResponse 응답
     */
    public BasicResponse logout(String number, String password) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            LogoutRequest logoutRequest = new LogoutRequest(number, password);
            UserCommandRequest req = new UserCommandRequest("AccountManager로그아웃", logoutRequest);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                System.out.println("[AccountManagerClient] ⭐ Singleton 인스턴스를 통한 로그아웃 서버 응답: " + r.code + " - " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 로그아웃 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * 사용자 역할 조회
     * 
     * @param number 사용자 번호
     * @return 역할 (Student, Professor, Admin)
     */
    public String getUserRole(String number) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            UserCommandRequest req = new UserCommandRequest("AccountManager역할조회", number);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                if (r.code.equals("200") && r.data instanceof String) {
                    return (String) r.data;
                }
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 역할 조회 실패: " + e.getMessage());
        }
        return null;
    }
}

