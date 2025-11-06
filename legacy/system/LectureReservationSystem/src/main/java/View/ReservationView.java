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
import Model.Reservation;

import java.util.List;
import java.util.Scanner;

public class ReservationView {
    private final ReservationController reservationController;

    public ReservationView(ReservationController reservationController) {
        this.reservationController = reservationController;
    }

    public void start(Scanner scanner) {
        while (true) {
            System.out.println("\n=== 강의실 예약 시스템 ===");
            System.out.println("1. 강의실 조회");
            System.out.println("2. 예약 취소");
            System.out.println("3. 뒤로가기");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            switch (choice) {
                case 1:
                    handleViewRooms(scanner); // 강의실 조회
                    break;
                case 2:
                    handleCancelReservation(scanner); // 예약 취소
                    break;
                case 3:
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return; // 메뉴 종료
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
            }
        }
    }

    // 강의실 예약 조회 메서드
    private void handleViewRooms(Scanner scanner) {
        System.out.print("조회할 강의실 번호 입력 (예: 912): ");
        String roomId = scanner.nextLine().trim();

        List<Reservation> reservations = reservationController.getReservationsByRoom(roomId);

        // 예약 내역 출력
        if (reservations.isEmpty()) {
            System.out.println("강의실 " + roomId + "에 대한 예약 내역이 없습니다.");
        } else {
            System.out.println("강의실 " + roomId + "의 예약 내역:");
            for (Reservation reservation : reservations) {
                System.out.println(" - " + reservation);
            }
        }
    }
    
    

    // 예약 취소 메서드
    private void handleCancelReservation(Scanner scanner) {
        System.out.print("취소할 강의실 번호를 입력하세요 (예: 912): ");
        String roomId = scanner.nextLine().trim();
        
        System.out.print("취소할 날짜를 입력하세요 (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        
        System.out.print("취소할 시작 시간을 입력하세요 (HH:MM): ");
        String startTime = scanner.nextLine().trim();

        boolean isCancelled = reservationController.cancelReservation(roomId, date, startTime);

        if (isCancelled) {
            System.out.println("예약이 성공적으로 취소되었습니다.");
        } else {
            System.out.println("해당 예약을 찾을 수 없습니다. 다시 확인해주세요.");
        }
    }
}