package deu;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress; // 추가
import java.net.URL; // 추가
import java.io.BufferedReader; // 추가
import java.io.InputStreamReader; // 추가

public class ServerMain {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("================================================================================");
            System.out.println("");
            System.out.println("    서버 실행 중... 포트 번호: " + PORT);
            System.out.println("");
            
            // ⭐⭐⭐ IP 주소 정보 출력 추가 ⭐⭐⭐
            printServerIPs(PORT);
            System.out.println("================================================================================");

            while (true) {
                Socket client = serverSocket.accept();
                // ClientHandler 클래스는 다른 파일에 정의되어 있으므로 그대로 사용
                new Thread(new ClientHandler(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 서버의 내부 IP와 외부 IP 주소를 확인하여 출력합니다.
     */
    private static void printServerIPs(int port) {
        try {
            // 1. 내부 IP (Local IP, 사설 IP) 확인
            String localIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("    [내부 접속용] 내부 IP 주소: " + localIp + ":" + port);

            // 2. 외부 IP (Public IP, 공인 IP) 확인
            String externalIp = getPublicIp();
            System.out.println("    [외부 접속용] 외부 IP 주소: " + externalIp + ":" + port);
            
            System.out.println("\n    클라이언트는 [외부 접속용] IP를 config.yaml에 설정해야 외부에서 접속 가능합니다.");

        } catch (Exception e) {
            System.err.println("    IP 주소 확인 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 외부 IP를 가져오는 메서드 (외부 API 사용)
     */
    private static String getPublicIp() {
        try {
            // 외부 서비스를 통해 공인 IP를 조회
            URL url = new URL("https://api.ipify.org");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine();
        } catch (Exception e) {
            return "외부 IP 불러오기 실패 (네트워크 확인)";
        }
    }
}