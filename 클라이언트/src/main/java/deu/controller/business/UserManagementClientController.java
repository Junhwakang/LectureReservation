package deu.controller.business;

import deu.command.*; // ⭐ 모든 Command 인터페이스 및 구현체 import
import deu.config.Config;
import deu.config.ConfigLoader;
import deu.model.dto.request.data.user.*;
import deu.model.dto.response.BasicResponse;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserManagementClientController {

    // 설정파일 불러오기 (기존 코드 유지)
    Config config = ConfigLoader.getConfig();
    String host = config.server.host;
    int port = config.server.port;

    // Singleton 인스턴스 (기존 코드 유지)
    @Getter
    private static final UserManagementClientController instance = new UserManagementClientController();

    private UserManagementClientController() {}

    /**
     * ⭐ 단일 책임 원칙 적용: Command 객체를 받아 서버로 전송하고 응답을 받는 공통 메서드
     */
    private BasicResponse sendCommand(UserCommand command) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            System.out.println("[Client] Command 객체 (" + command.getClass().getSimpleName() + ") 서버로 전송...");
            
            // Command 객체 전송 (직렬화)
            out.writeObject(command);

            // BasicResponse 객체 수신
            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                return r;
            }
        } catch (Exception e) {
            System.err.println("서버 통신 실패: " + e.getMessage());
        }
        return new BasicResponse("503", "서버 통신 실패 또는 응답 오류");
    }

    // ⭐ 1. 사용자 정보 수정 처리 (커맨드 패턴 적용)
    public BasicResponse updateUser(String number, String password, String name, String major) {
        // DTO 생성
        UserDataModificationRequest request = new UserDataModificationRequest(number, password, name, major);
        // Command 객체 생성 및 DTO 캡슐화
        UserCommand command = new UpdateUserCommand(request);
        return sendCommand(command);
    }

    // ⭐ 2. 사용자 삭제 처리 (커맨드 패턴 적용)
    public BasicResponse deleteUser(String number) {
        DeleteRequest request = new DeleteRequest(number);
        UserCommand command = new DeleteUserCommand(request);
        return sendCommand(command);
    }

    // ⭐ 3. 사용자 단일 조회 처리 (커맨드 패턴 적용)
    public BasicResponse findUser(String number) {
        FindRequest request = new FindRequest(number);
        UserCommand command = new FindUserCommand(request);
        return sendCommand(command);
    }

    // ⭐ 4. 전체 사용자 목록 조회 처리 (커맨드 패턴 적용)
    public BasicResponse findAllUsers() {
        UserCommand command = new FindAllUsersCommand(); // DTO가 필요 없음
        return sendCommand(command);
    }

}