package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 커맨드 패턴 - 예약 거부 커맨드
 * SFR-410, SFR-411, SFR-412: 관리자가 예약을 거부하고 사용자에게 알림을 전달하는 기능
 */
public class RejectReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final String rejectionReason; // SFR-411, SFR-412: 거부 원인
    private final ReservationRepository repository;
    private final ReservationSubject subject;
    private String previousStatus; // undo를 위한 이전 상태

    public RejectReservationCommand(String reservationId, String rejectionReason, ReservationSubject subject) {
        this.reservationId = reservationId;
        this.rejectionReason = rejectionReason;
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

            // 상태 변경: 대기 -> 거부
            target.setStatus("거부");
            repository.saveToFile();

            // 옵저버에게 알림 (SFR-410: 예약 거부 알림 + 거부 원인)
            String message = "예약이 거부되었습니다.";
            if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
                message += " [거부 원인: " + rejectionReason + "]";
            }
            subject.notifyObservers(target, message);

            return new BasicResponse("200", "예약이 거부되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 거부 중 오류가 발생했습니다: " + e.getMessage());
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
                return new BasicResponse("200", "예약 거부가 취소되었습니다.");
            }
            return new BasicResponse("404", "예약을 찾을 수 없습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
