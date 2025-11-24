package deu;

import deu.controller.SystemController;
import deu.model.dto.response.BasicResponse;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            // 순서 주의: 반드시 OutputStream을 먼저 생성
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // flush header
            in = new ObjectInputStream(socket.getInputStream());

            // 요청 수신
            Object request = in.readObject();

            // 요청 처리
            Object response = new SystemController().handle(request);

            // 응답 송신
            out.writeObject(response);
            out.flush();

        } catch (Exception e) {
            System.err.println("[ClientHandler] 통신 중 오류 발생:");
            e.printStackTrace();

            // ➕ 에러 응답 시도 (out이 이미 만들어졌을 때만)
            try {
                if (out != null && !socket.isClosed()) {
                    BasicResponse errorRes
                            = new BasicResponse("500", "서버 예외: " + e.toString());
                    out.writeObject(errorRes);
                    out.flush();
                }
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
