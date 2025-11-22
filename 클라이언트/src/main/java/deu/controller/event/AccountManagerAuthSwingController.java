package deu.controller.event;

import deu.config.Config;
import deu.config.ConfigLoader;
import deu.controller.business.AccountManagerClientController;
import deu.model.dto.response.BasicResponse;
import deu.view.Auth;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

/**
 * ⭐⭐⭐ Singleton 패턴 사용 - AccountManagerAuthSwingController ⭐⭐⭐
 * 
 * 이 컨트롤러는 AccountManagerClientController의 Singleton 인스턴스를 사용합니다.
 * 
 * Singleton 패턴 활용:
 * - AccountManagerClientController.getInstance()를 통해 Singleton 인스턴스 사용
 * - 여러 뷰에서 동일한 AccountManagerClientController 인스턴스를 공유
 * 
 * @author AccountManager Team
 * @version 1.0
 */
public class AccountManagerAuthSwingController {
    private final Auth view;
    // ⭐ Singleton 패턴: AccountManagerClientController의 Singleton 인스턴스 사용
    private final AccountManagerClientController accountManagerClient;

    public AccountManagerAuthSwingController(Auth view) {
        this.view = view;
        // ⭐ Singleton 패턴: getInstance()를 통한 단일 인스턴스 획득
        this.accountManagerClient = AccountManagerClientController.getInstance();

        // 이벤트 연결
        view.addLoginListener(this::handleLogin);
        view.addSignupListener(this::handleSignup);
        view.addSwitchToSignupListener(this::switchToSignup);
        view.addSwitchToLoginListener(this::switchToLogin);
        view.addGithubServerButtonListener(this::handleGithubServer);
        view.addGithubClientButtonListener(this::handleGithubClient);
        view.addSaveConfigButtonListener(this::handleSetHostPort);
        view.addMyConfigPanelInitListener(createConfigPanelInitListener());
    }

    /**
     * ⭐ Singleton 패턴을 사용한 로그인 버튼 기능
     * AccountManagerClientController의 Singleton 인스턴스를 통해 로그인 처리
     */
    private void handleLogin(ActionEvent e) {
        System.out.println("[AccountManagerAuth] ⭐ Singleton 패턴을 사용한 로그인 버튼 클릭됨");
        
        String id = view.getLoginId();
        String pw = view.getLoginPassword();
        
        System.out.println("[AccountManagerAuth] 입력된 ID: " + (id != null ? id : "null"));
        System.out.println("[AccountManagerAuth] 입력된 PW: " + (pw != null ? "***" : "null"));

        // ID 규칙 안내
        if (id == null || id.isEmpty()) {
            view.showError("ID를 입력해주세요.");
            return;
        }
        
        char firstChar = id.charAt(0);
        if (firstChar != 's' && firstChar != 'S' && 
            firstChar != 'p' && firstChar != 'P' && 
            firstChar != 'm' && firstChar != 'M') {
            view.showError("ID는 s(학생), p(교수), m(관리자)로 시작해야 합니다.");
            return;
        }

        try {
            System.out.println("[AccountManagerAuth] ⭐ Singleton 인스턴스를 통한 서버에 로그인 요청 전송 중...");
            BasicResponse res = accountManagerClient.login(id, pw);
            System.out.println("[AccountManagerAuth] 서버 응답 수신: " + (res != null ? "code=" + res.code + ", data=" + res.data : "null"));

            if (res != null && res.code.equals("200")) {
                // 로그인 성공 - ID의 첫 글자로 역할 판단
                String role = "Student"; // 기본값
                if (firstChar == 's' || firstChar == 'S') {
                    role = "Student";
                } else if (firstChar == 'p' || firstChar == 'P') {
                    role = "Professor";
                } else if (firstChar == 'm' || firstChar == 'M') {
                    role = "Admin";
                }
                
                // 역할별 메뉴 출력
                String roleMessage = getRoleMessage(role);
                System.out.println("[AccountManagerAuth] ⭐ Singleton 패턴을 통한 로그인 성공! 역할: " + role);
                view.showSuccess("로그인 성공!\n역할: " + roleMessage);
                view.transitionToHome(id, pw);
            } else if (res != null && res.code.equals("401")) {
                System.out.println("[AccountManagerAuth] 로그인 실패: 비밀번호 오류");
                view.showError("로그인 실패: " + res.data); // 비밀번호 오류
            } else if (res != null && res.code.equals("404")) {
                System.out.println("[AccountManagerAuth] 로그인 실패: 존재하지 않는 사용자");
                view.showError("로그인 실패: " + res.data); // 존재하지 않는 사용자
            } else {
                System.out.println("[AccountManagerAuth] 로그인 실패: 통신 오류 - " + (res != null ? res.code + " - " + res.data : "응답 null"));
                view.showError("통신 오류\n" + (res != null ? res.data : "Host IP와 연결되지 않습니다\n올바른 Host IP와 Port를 작성해주세요."));
            }

        } catch (Exception ex) {
            System.err.println("[AccountManagerAuth] 예외 발생: " + ex.getMessage());
            ex.printStackTrace();
            view.showError("서버와의 통신 중 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.\n오류: " + ex.getMessage());
        }
    }

    /**
     * ⭐ Singleton 패턴을 사용한 회원 가입 버튼 기능
     * AccountManagerClientController의 Singleton 인스턴스를 통해 회원가입 처리
     */
    private void handleSignup(ActionEvent e) {
        String id = view.getSignupId();
        String pw = view.getSignupPassword();
        String name = view.getSignupName();
        String major = view.getSignupMajor();
        String rolePrefix = view.getSignupRole(); // s, p, m 중 하나
        
        // 입력 검증
        if (id == null || id.isEmpty()) {
            view.showError("ID를 입력해주세요.");
            return;
        }
        
        if (pw == null || pw.isEmpty()) {
            view.showError("비밀번호를 입력해주세요.");
            return;
        }
        
        if (name == null || name.isEmpty()) {
            view.showError("이름을 입력해주세요.");
            return;
        }
        
        if (major == null || major.isEmpty()) {
            view.showError("전공을 입력해주세요.");
            return;
        }
        
        // 역할에 따라 ID 앞에 접두사 추가
        String finalId = rolePrefix + id;
        
        // 역할 메시지
        String roleMessage = "";
        if (rolePrefix.equals("s")) {
            roleMessage = "학생";
        } else if (rolePrefix.equals("p")) {
            roleMessage = "교수";
        } else if (rolePrefix.equals("m")) {
            roleMessage = "관리자";
        }
        
        System.out.println("[AccountManagerAuth] ⭐ Singleton 인스턴스를 통한 회원가입 요청: " + finalId);
        BasicResponse res = accountManagerClient.signup(finalId, pw, name, major);

        if (res != null && res.code.equals("200")) {
            view.showSuccess("회원가입 성공!\n역할: " + roleMessage);
            view.switchToLoginPanel();
        } else {
            view.showError("회원가입 실패: " + (res != null ? res.data : "서버 오류"));
        }
    }
    
    /**
     * 역할 메시지 반환
     */
    private String getRoleMessage(String role) {
        switch (role) {
            case "Student":
                return "학생";
            case "Professor":
                return "교수";
            case "Admin":
                return "관리자";
            default:
                return role;
        }
    }

    // 깃허브 이동 버튼 기능
    private void handleGithubServer(ActionEvent e) {
        openWebpage("https://github.com/Junhwakang/LectureReservation");
    }
    
    private void handleGithubClient(ActionEvent e) {
        openWebpage("https://github.com/Junhwakang/LectureReservation");
    }

    // 회원 가입 패널 전환
    private void switchToSignup(ActionEvent e) {
        System.out.println("[AccountManagerAuth] 회원가입 화면으로 전환");
        view.switchToSignupPanel();
    }

    // 로그인 패널 전환
    private void switchToLogin(ActionEvent e) {
        System.out.println("[AccountManagerAuth] 취소 버튼 클릭됨 - 로그인 화면으로 전환");
        view.switchToLoginPanel();
    }

    // 아이피 포트 설정
    private void handleSetHostPort(ActionEvent e) {
        String host = view.getHostField();
        String portText = view.getPortField();

        // 기본값 처리
        if (host.isEmpty()) {
            host = "localhost";
        }

        int port;
        try {
            port = portText.isEmpty() ? 9999 : Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            view.showError("포트는 숫자여야 합니다.");
            return;
        }

        // 설정 파일 수정 및 저장
        String finalHost = host;
        int finalPort = port;
        ConfigLoader.updateConfig(c -> {
            c.server.host = finalHost;
            c.server.port = finalPort;
        });

        view.showSuccess("설정이 저장되었습니다.\nIP: " + host + "\nPort: " + port);
    }

    private void openWebpage(String urlString) {
        try {
            URI uri = new URI(urlString);
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            System.err.println("웹페이지 열기 실패: " + e.getMessage());
        }
    }

    private AncestorListener createConfigPanelInitListener() {
        return new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                Config config = ConfigLoader.getConfig();
                if (view.getHostField() != null) {
                    view.getHostField().setText(config.server.host);
                }
                if (view.getPortField() != null) {
                    view.getPortField().setText(String.valueOf(config.server.port));
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        };
    }
}

