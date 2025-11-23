package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

import java.util.List;

/**
 * 커맨드 패턴 - 예약 승인 커맨드
 * SFR-409, SFR-410: 관리자가 예약을 승인하고 사용자에게 알림을 전달하는 기능
 */
public class ApproveReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final ReservationRepository repository;
    private final ReservationSubject subject;
    private String previousStatus; // undo를 위한 이전 상태

    public ApproveReservationCommand(String reservationId, ReservationSubject subject) {
        this.reservationId = reservationId;
        this.repository = ReservationRepository.getInstance();
        this.subject = subject;
    }

    @Override
    public BasicResponse execute() {
        try {
            RoomReservation target = repository.findById(reservationId);
            if (target == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }

            // 이전 상태 저장
            previousStatus = target.getStatus();

            // 중복 예약 체크 (승인 전)
            boolean isDuplicate = repository.isDuplicate(
                    target.getDate(),
                    target.getStartTime(),
                    target.getLectureRoom()
            );

            // 본인 예약은 제외하고 중복 체크
            if (isDuplicate) {
                List<RoomReservation> duplicates = repository.findAll().stream()
                        .filter(r -> r.getDate().equals(target.getDate()) &&
                                r.getStartTime().equals(target.getStartTime()) &&
                                r.getLectureRoom().equals(target.getLectureRoom()) &&
                                !r.getId().equals(reservationId) &&
                                "승인".equals(r.getStatus()))
                        .toList();

                if (!duplicates.isEmpty()) {
                    return new BasicResponse("409", "해당 시간에 이미 승인된 예약이 존재합니다.");
                }
            }

            // 상태 변경: 대기 -> 승인
            target.setStatus("승인");
            repository.saveToFile();

            // 옵저버에게 알림 (SFR-409: 예약 승인 알림)
            subject.notifyObservers(target, "예약이 승인되었습니다.");

            return new BasicResponse("200", "예약 상태가 승인으로 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public BasicResponse undo() {
        if (previousStatus == null) {
            return new BasicResponse("400", "되돌릴 이전 상태가 없습니다.");
        }

        try {
            RoomReservation target = repository.findById(reservationId);
            if (target != null) {
                target.setStatus(previousStatus);
                repository.saveToFile();
                return new BasicResponse("200", "예약 승인이 취소되었습니다.");
            }
            return new BasicResponse("404", "예약을 찾을 수 없습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
