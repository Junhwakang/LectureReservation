package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 예약 거부 커맨드 (SFR-403, SFR-404)
 * 관리자가 예약을 거부하고 거부 원인을 저장하는 작업을 캡슐화
 */
public class RejectReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final String rejectionReason; // 거부 원인
    private final ReservationSubject subject;
    private final ReservationRepository repository;
    
    // Undo를 위한 이전 상태 저장
    private String previousStatus;
    private String previousRejectionReason;
    
    public RejectReservationCommand(String reservationId, String rejectionReason, ReservationSubject subject) {
        this.reservationId = reservationId;
        this.rejectionReason = rejectionReason;
        this.subject = subject;
        this.repository = ReservationRepository.getInstance();
    }
    
    @Override
    public BasicResponse execute() {
        try {
            RoomReservation target = repository.findById(reservationId);
            if (target == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }
            
            // 이전 상태 저장 (Undo용)
            previousStatus = target.getStatus();
            previousRejectionReason = target.getRejectionReason();
            
            // 상태 변경 및 거부 원인 저장 (SFR-404)
            target.setStatus("거부");
            target.setRejectionReason(rejectionReason);
            repository.saveToFile();
            
            // 옵저버에게 거부 알림 (SFR-409: 예약 거부 시 사용자 알림)
            subject.notifyReservationRejected(target, rejectionReason);
            
            return new BasicResponse("200", "예약이 거부되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 거부 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public BasicResponse undo() {
        if (previousStatus == null) {
            return new BasicResponse("400", "Undo할 데이터가 없습니다.");
        }
        
        try {
            RoomReservation target = repository.findById(reservationId);
            if (target == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }
            
            // 이전 상태로 복원
            target.setStatus(previousStatus);
            target.setRejectionReason(previousRejectionReason);
            repository.saveToFile();
            
            return new BasicResponse("200", "예약 거부가 취소되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "Undo 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public String getDescription() {
        return "예약 거부: ID=" + reservationId + ", 원인=" + rejectionReason;
    }
}
