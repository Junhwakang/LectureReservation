// deu.command.FindAllUsersCommand.java
package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.service.UserService;

public class FindAllUsersCommand implements UserCommand {
    private static final long serialVersionUID = 1L;

    private transient UserService receiver; 

    public FindAllUsersCommand() {
    }

    @Override
    public BasicResponse orderUp() {
        if (receiver == null) {
            this.receiver = UserService.getInstance();
        }
        System.out.println("FindAllUsersCommand: Receiver에 작업 위임 (findAll)...");
        return receiver.findAll();
    }
}