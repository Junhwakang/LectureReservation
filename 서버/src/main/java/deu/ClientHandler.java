package deu;

import deu.controller.SystemController;
import deu.model.dto.response.BasicResponse;
// [필수 import]
import deu.controller.business.UserController;
import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.command.UserCommandRequest;
import deu.model.dto.request.data.user.LogoutRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Object request = null;
        boolean isProcessingSuccess = false; // [핵심] 로그인 처리 성공 여부

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // 1. 요청 수신
            request = in.readObject();

            // 2. 요청 처리 (대기열에 걸리면 여기서 멈춤)
            Object response = new SystemController().handle(request);
            
            // [중요] 처리 결과가 "200(성공)"이면 성공 깃발을 듭니다.
            if (response instanceof BasicResponse && "200".equals(((BasicResponse) response).code)) {
                isProcessingSuccess = true;
            } else if (response instanceof deu.model.dto.response.CurrentResponse) {
                isProcessingSuccess = true; // 조회 요청 등은 성공으로 간주
            }

            // 3. 응답 전송
            out.writeObject(response);
            out.flush();

        } catch (EOFException | SocketException e) {
            // [수정된 로직] 로그인이 성공하지 않았는데 끊겼을 때만 삭제합니다!
            if (!isProcessingSuccess) {
                handleGhostUser(request);
            }
        } catch (Exception e) {
            System.err.println("⚠ [통신 오류] " + e.toString());
            
            if (!isProcessingSuccess) {
                handleGhostUser(request);
            }

            try {
                if (out != null && !socket.isClosed()) {
                    out.writeObject(new BasicResponse("500", "서버 오류"));
                    out.flush();
                }
            } catch (IOException ignored) {}

        } finally {
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            try { if (out != null) out.close(); } catch (IOException ignored) {}
            try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        }
    }

    private void handleGhostUser(Object request) {
        try {
            if (request instanceof UserCommandRequest) {
                UserCommandRequest cmd = (UserCommandRequest) request;
                if ("로그인".equals(cmd.command) && cmd.payload instanceof LoginRequest) {
                    LoginRequest loginReq = (LoginRequest) cmd.payload;
                    System.out.println("🧹 [청소] 대기 중 이탈한 사용자(" + loginReq.number + ")를 명단에서 제거합니다.");
                    UserController.getInstance().handleLogout(new LogoutRequest(loginReq.number, null));
                }
            }
        } catch (Exception e) {
            System.err.println("정리 중 오류: " + e.getMessage());
        }
    }
}