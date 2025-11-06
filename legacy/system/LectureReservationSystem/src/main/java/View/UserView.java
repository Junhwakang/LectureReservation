package View;

import Controller.UserController;
import Controller.ReservationController;

import java.util.Scanner;

public class UserView {
    private final UserController userController;

    public UserView(UserController userController) {
        this.userController = userController;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== 사용자 시스템 ===");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 종료");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 클리어

            switch (choice) {
                case 1:
                    if (handleLogin(scanner)) {
                        // 로그인 성공 시 LectureView 실행
                        ReservationController reservationController = new ReservationController();
                        LectureView lectureView = new LectureView(reservationController);
                        lectureView.start(scanner); // 강의실 예약 시스템으로 이동
                    }
                    break;
                case 2:
                    handleRegister(scanner); // 회원가입 기능
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다.");
                    return; // 프로그램 종료
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
            }
        }
    }

    // 로그인 로직
    private boolean handleLogin(Scanner scanner) {
        System.out.print("아이디 입력: ");
        String username = scanner.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = scanner.nextLine();

        if (userController.login(username, password)) {
            System.out.println("로그인 성공! 환영합니다, " + username + "님.");
            return true;
        } else {
            System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
            return false;
        }
    }

    // 회원가입 로직
    private void handleRegister(Scanner scanner) {
        System.out.print("아이디 입력 (중복 불가): ");
        String username = scanner.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = scanner.nextLine();

        if (userController.register(username, password)) {
            System.out.println("회원가입 완료! 이제 로그인하세요.");
        } else {
            System.out.println("회원가입 실패: 이미 존재하는 아이디입니다.");
        }
    }
}
