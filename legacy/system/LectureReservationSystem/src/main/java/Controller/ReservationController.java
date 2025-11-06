/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author jun
 */
import Model.Reservation;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReservationController {
    private final String reservationsFilePath = "reservations.txt"; // 예약 정보 파일 경로
    private List<Reservation> reservations; // 메모리 내 예약 데이터 관리

    public ReservationController() {
        reservations = new ArrayList<>();
        loadReservationsFromFile(); // 파일에서 예약 내역 로드
    }

    // 파일에서 예약 데이터 로드
    private void loadReservationsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(reservationsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // roomId,date,startTime,endTime,reservedBy
                    reservations.add(new Reservation(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("예약 파일을 찾을 수 없습니다. 새 파일을 생성합니다: " + reservationsFilePath);
            // 파일이 없는 경우 새로 작성
            saveReservationsToFile();
        } catch (IOException e) {
            System.err.println("파일 읽기 중 오류: " + e.getMessage());
        }
    }

    // 데이터를 파일에 저장
    private void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reservationsFilePath))) {
            for (Reservation reservation : reservations) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        reservation.getRoomId(), reservation.getDate(),
                        reservation.getStartTime(), reservation.getEndTime(),
                        reservation.getReservedBy()));
            }
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류: " + e.getMessage());
        }
    }
    
    // [추가] 강의실 예약 기능
    public boolean reserveRoom(String roomId, String date, String startTime, String endTime, String reservedBy) {
        // 기존 예약 내역과 겹치는지 확인
        for (Reservation reservation : reservations) {
            if (reservation.getRoomId().equals(roomId) && reservation.getDate().equals(date)) {
                // 시간 겹침 여부를 검토
                if (!(endTime.compareTo(reservation.getStartTime()) <= 0 || startTime.compareTo(reservation.getEndTime()) >= 0)) {
                    // 예약 시간 겹침
                    return false;
                }
            }
        }

        // 예약 가능, 새 예약 저장
        Reservation newReservation = new Reservation(roomId, date, startTime, endTime, reservedBy);
        reservations.add(newReservation);
        saveReservationsToFile(); // 파일에 업데이트
        return true; // 예약 성공
    }

    // 강의실 예약 조회
    public List<Reservation> getReservationsByRoom(String roomId) {
        List<Reservation> roomReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getRoomId().equals(roomId)) {
                roomReservations.add(reservation);
            }
        }
        return roomReservations;
    }

    // 예약 취소
    public boolean cancelReservation(String roomId, String date, String startTime) {
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getRoomId().equals(roomId) &&
                reservation.getDate().equals(date) &&
                reservation.getStartTime().equals(startTime)) {
                iterator.remove(); // 예약 삭제
                saveReservationsToFile(); // 파일 동기화
                return true; // 취소 성공
            }
        }
        return false; // 취소 실패
    }
}