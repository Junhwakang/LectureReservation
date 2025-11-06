/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

/**
 *
 * @author jun
 */
import Controller.ReservationController;

import java.util.Scanner;

public class LectureView {
    private ReservationController reservationController;

    public LectureView(ReservationController reservationController) {
        this.reservationController = reservationController;
    }

    public void start(Scanner scanner) {
        while (true) {
            System.out.println("\n=== 강의실 예약 시스템 ===");
            System.out.println("1. 강의실 조회");
            System.out.println("2. 강의실 예약");
            System.out.println("3. 예약 취소");
            System.out.println("4. 로그아웃");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleViewRooms(scanner);
                    break;
                case 2:
                    handleReserveRoom(scanner);
                    break;
                case 3:
                    handleCancelReservation(scanner);
                    break;
                case 4:
                    System.out.println("로그아웃 완료. 메인 메뉴로 돌아갑니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택하세요.");
            }
        }
    }

    private void handleViewRooms(Scanner scanner) {
        System.out.print("조회할 강의실 번호 입력 (예: 912): ");
        String roomId = scanner.nextLine();

        var reservations = reservationController.getReservationsByRoom(roomId);
        System.out.println("강의실 " + roomId + "의 예약 내역:");
        if (!reservations.isEmpty()) {
            reservations.forEach(System.out::println);
        } else {
            System.out.println("환영합니다. 예약 내역이 없습니다.");
        }
    }

    // [추가] 강의실 예약 로직
    private void handleReserveRoom(Scanner scanner) {
        // 사용자 입력
        System.out.print("예약할 강의실 번호 입력 (예: 912): ");
        String roomId = scanner.nextLine();
        System.out.print("날짜 입력 (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("시작 시간 입력 (HH:MM): ");
        String startTime = scanner.nextLine();
        System.out.print("종료 시간 입력 (HH:MM): ");
        String endTime = scanner.nextLine();
        System.out.print("예약자 이름 입력: ");
        String reservedBy = scanner.nextLine();

        boolean result = reservationController.reserveRoom(roomId, date, startTime, endTime, reservedBy);

        if (result) {
            System.out.println("예약이 성공적으로 완료되었습니다.");
        } else {
            System.out.println("예약 실패: 해당 시간대에 강의실이 이미 예약되어 있습니다.");
        }
    }

    private void handleCancelReservation(Scanner scanner) {
        System.out.print("취소할 강의실 번호 입력 (예: 912): ");
        String roomId = scanner.nextLine();
        System.out.print("날짜 입력 (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("취소할 시작 시간 입력 (HH:MM): ");
        String startTime = scanner.nextLine();

        boolean result = reservationController.cancelReservation(roomId, date, startTime);

        if (result) {
            System.out.println("예약이 성공적으로 취소되었습니다.");
        } else {
            System.out.println("해당 예약을 찾을 수 없습니다.");
        }
    }
}