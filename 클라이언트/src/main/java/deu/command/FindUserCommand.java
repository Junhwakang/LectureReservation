// deu.command.FindUserCommand.java
package deu.command;

import deu.model.dto.request.data.user.FindRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;

public class FindUserCommand implements UserCommand {
    private static final long serialVersionUID = 1L;

    private final FindRequest payload;
    private transient UserService receiver; 

    public FindUserCommand(FindRequest payload) {
        this.payload = payload;
    }

    @Override
    public BasicResponse orderUp() {
        if (receiver == null) {
            this.receiver = UserService.getInstance();
        }
        System.out.println("FindUserCommand: Receiver에 작업 위임 (find)...");
        return receiver.find(payload);
    }
}