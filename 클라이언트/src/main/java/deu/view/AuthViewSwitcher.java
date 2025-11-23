package deu.view;

import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * 인증 관련 뷰 간 전환을 담당하는 유틸리티 클래스입니다.
 */
public class AuthViewSwitcher {
    private static JPanel container;
    private static CardLayout cardLayout;

    /**
     * 뷰 전환기를 초기화합니다.
     * 
     * @param container 뷰가 표시될 컨테이너 패널
     * @param cardLayout 카드 레이아웃 매니저
     */
    public static void init(JPanel container, CardLayout cardLayout) {
        AuthViewSwitcher.container = container;
        AuthViewSwitcher.cardLayout = cardLayout;
    }

    /**
     * 지정된 뷰로 전환합니다.
     * 
     * @param viewName 전환할 뷰의 이름
     */
    public static void showView(String viewName) {
        if (cardLayout != null && container != null) {
            cardLayout.show(container, viewName);
        }
    }

    /**
     * 로그인 뷰로 전환합니다.
     */
    public static void showLoginView() {
        showView("login");
    }

    /**
     * 회원가입 뷰로 전환합니다.
     */
    public static void showSignupView() {
        showView("signup");
    }

    /**
     * 설정 뷰로 전환합니다.
     */
    public static void showConfigView() {
        showView("config");
    }
}
