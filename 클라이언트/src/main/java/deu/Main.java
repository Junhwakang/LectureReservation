package deu;

import deu.controller.event.AccountManagerAuthSwingController;
import deu.view.Auth;

/**
 * ⭐⭐⭐ Singleton 패턴 사용 - Main 클래스 ⭐⭐⭐
 * 
 * AccountManagerAuthSwingController를 사용하여 Singleton 패턴을 활용합니다.
 * 
 * @author AccountManager Team
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Auth authView = new Auth(); // authView 생성
            // ⭐ Singleton 패턴: AccountManagerAuthSwingController 사용
            // 내부에서 AccountManagerClientController.getInstance()를 통해 Singleton 인스턴스 사용
            new AccountManagerAuthSwingController(authView);
            authView.setVisible(true); // 창 띄우기
        });
    }
}