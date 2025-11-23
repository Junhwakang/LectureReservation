// deu.command.DeleteUserCommand.java
package deu.command;

import deu.model.dto.request.data.user.DeleteRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;

public class DeleteUserCommand implements UserCommand {
    private static final long serialVersionUID = 1L;

    private final DeleteRequest payload;
    private transient UserService receiver; 

    public DeleteUserCommand(DeleteRequest payload) {
        this.payload = payload;
    }

    @Override
    public BasicResponse orderUp() {
        if (receiver == null) {
            this.receiver = UserService.getInstance();
        }
        System.out.println("DeleteUserCommand: Receiver에 작업 위임 (delete)...");
        return receiver.delete(payload);
    }
}