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
 * YAML 파일(users.yaml)을 사용하도록 수정됨
 */
public class AccountManagerClientController {
    
    private static AccountManagerClientController instance;
    
    private final Config config = ConfigLoader.getConfig();
    private final String host = config.server.host;
    private final int port = config.server.port;
    
    private AccountManagerClientController() {
        System.out.println("[AccountManagerClientController] ⭐ Singleton 인스턴스 생성 완료 (YAML 사용)");
    }
    
    public static synchronized AccountManagerClientController getInstance() {
        if (instance == null) {
            instance = new AccountManagerClientController();
        }
        return instance;
    }
    
    /**
     * YAML 기반 로그인 요청
     * 
     * ID 규칙:
     * - s로 시작: Student (학생)
     * - p로 시작: Professor (교수)
     * - m으로 시작: Admin (관리자)
     */
    public BasicResponse login(String number, String password) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            LoginRequest loginRequest = new LoginRequest(number, password);
            UserCommandRequest req = new UserCommandRequest("AccountManager로그인", loginRequest);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                System.out.println("[AccountManagerClient] ⭐ YAML 기반 서버 응답: " + r.code + " - " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * YAML 기반 회원가입 요청
     * 
     * ID 규칙:
     * - s로 시작: Student (학생)
     * - p로 시작: Professor (교수)
     * - m으로 시작: Admin (관리자)
     */
    public BasicResponse signup(String number, String password, String name, String major) {
        // 클라이언트 측에서도 ID 규칙 검증
        if (number == null || number.isEmpty()) {
            return new BasicResponse("400", "ID를 입력해주세요.");
        }
        
        String firstChar = number.toLowerCase().substring(0, 1);
        if (!firstChar.equals("s") && !firstChar.equals("p") && !firstChar.equals("m")) {
            return new BasicResponse("400", "ID는 s(학생), p(교수), m(관리자)로 시작해야 합니다.");
        }
        
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            SignupRequest signupRequest = new SignupRequest(number, password, name, major);
            UserCommandRequest req = new UserCommandRequest("AccountManager회원가입", signupRequest);
            out.writeObject(req);
            out.flush();

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                System.out.println("[AccountManagerClient] ⭐ YAML 기반 서버 응답: " + r.code + " - " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * 로그아웃 요청
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
                System.out.println("[AccountManagerClient] ⭐ YAML 기반 로그아웃 서버 응답: " + r.code + " - " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.err.println("[AccountManagerClient] 로그아웃 서버 통신 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return new BasicResponse("500", "서버 통신 실패");
    }
    
    /**
     * 사용자 역할 조회 (ID 첫 글자 기반)
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