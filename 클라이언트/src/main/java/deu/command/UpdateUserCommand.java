// deu.command.UpdateUserCommand.java
package deu.command;

import deu.model.dto.request.data.user.UserDataModificationRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService; 

public class UpdateUserCommand implements UserCommand {
    private static final long serialVersionUID = 1L;

    // 클라이언트에서 전송되는 요청 DTO
    private final UserDataModificationRequest payload; 
    
    // Receiver는 서버에서 transient로 선언 및 초기화
    private transient UserService receiver; 

    // ⭐⭐ 생성자 수정: DTO 객체를 인수로 받음 (오류 해결)
    public UpdateUserCommand(UserDataModificationRequest payload) {
        this.payload = payload;
    }

    @Override
    public BasicResponse orderUp() {
        if (receiver == null) {
            this.receiver = UserService.getInstance(); 
        }
        System.out.println("UpdateUserCommand: Receiver에 작업 위임 (update)...");
        // UserService.update(payload) 호출
        return receiver.update(payload); 
    }
}