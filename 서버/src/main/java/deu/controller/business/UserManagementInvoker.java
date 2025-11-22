// deu.controller.business.UserManagementInvoker.java
package deu.controller.business;

import deu.command.UserCommand;
import deu.model.dto.response.BasicResponse;

public class UserManagementInvoker {
    
    // Singleton 구현
    private static final UserManagementInvoker instance = new UserManagementInvoker();

    private UserManagementInvoker() {
        // Invoker 초기화
    }
    
    public static UserManagementInvoker getInstance() {
        return instance;
    }
    
    // Command 객체를 받아 orderUp() 메서드를 호출합니다.
    public BasicResponse executeCommand(UserCommand command) {
        System.out.println("[Invoker] Command 객체의 orderUp() 요청.");
        // Command의 orderUp() 호출 (실제 로직은 Command 내부에서 Receiver에 위임됨)
        BasicResponse result = command.orderUp();
        System.out.println("[Invoker] Command 실행 완료.");
        return result;
    }
}