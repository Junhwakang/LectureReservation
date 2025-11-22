package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 예약 승인 커맨드 (SFR-403, SFR-404)
 * 관리자가 예약을 승인하는 작업을 캡슐화
 */
public class ApproveReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final ReservationSubject subject;
    private final ReservationRepository repository;
    
    // Undo를 위한 이전 상태 저장
    private String previousStatus;
    
    public ApproveReservationCommand(String reservationId, ReservationSubject subject) {
        this.reservationId = reservationId;
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
            
            // 상태 변경 (SFR-404)
            target.setStatus("승인");
            repository.saveToFile();
            
            // 옵저버에게 승인 알림 (SFR-409: 예약 승인 시 사용자 알림)
            subject.notifyReservationApproved(target);
            
            return new BasicResponse("200", "예약 상태가 승인으로 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 승인 중 오류가 발생했습니다: " + e.getMessage());
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
            repository.saveToFile();
            
            return new BasicResponse("200", "예약 승인이 취소되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "Undo 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public String getDescription() {
        return "예약 승인: ID=" + reservationId;
    }
}
