/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import java.util.List;

/**
 *
 * @author User
 */

/**
 * SFR-216
 * 서버는 교수가 이미 예약한 시간에는
 * 다른 예약(보강, 세미나)이 신청되지 않도록 검증해야 한다.
 *
 * 교수 예약이 우선(Professor First)이기 때문에
 * 이미 같은 시간/강의실에 교수 예약이 있으면
 * 학생/세미나 예약은 거절한다.
 */
public class ProfFirstStrategy implements ReservationValidationStrategy {
    
    @Override
    public void validate(RoomReservationRequest payload,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        // 1) 예약자 자체가 교수면 이 부분은 SFR-210에서 처리
        if (isProfessor(payload.getNumber())) {
            return;
            
        }// 2) 필수 정보 없으면 이 판단 불가 --> 패스
        if (payload.getDate() == null ||
                payload.getStartTime() == null ||
                payload.getBuildingName() == null ||
                payload.getFloor() == null ||
                payload.getLectureRoom() == null) {
            return;
        }

        String date = payload.getDate();                // 날짜
        String startTime = payload.getStartTime();      //시작 시간
        String building = payload.getBuildingName();    //건물
        String floor = payload.getFloor();              // 층
        String room = payload.getLectureRoom();         // 강의실

        // 3) 전체 예약에서 "해당 시간 + 강의실에 교수 예약" 이 있는지 검사
        List<RoomReservation> all = repo.findAll();

        for (RoomReservation r : all) {
            // 교수 예약이 아닌 건 패스
            if (!isProfessor(r.getNumber())) continue;

            // 취소된 예약은 무시
            if ("취소".equals(r.getStatus())) continue;

            // 같은 날짜 / 같은 건물 / 같은 층 / 같은 강의실 / 같은 시작시간
            if (date.equals(r.getDate())
                    && startTime.equals(r.getStartTime())
                    && building.equals(r.getBuildingName())
                    && floor.equals(r.getFloor())
                    && room.equals(r.getLectureRoom())) {

                // 이미 교수 예약이 있으므로 다른 목적 예약은 불가
                throw new ReservationValidationException(
                        "해당 시간에는 교수의 예약이 존재하여 신청할 수 없습니다");
                //SFR-216 패턴 적용
            }
        }
    }

    private boolean isProfessor(String number) {
        if (number == null || number.isEmpty()) return false;
        char c = number.charAt(0);
        return c == 'p' || c == 'P';
    }
}
