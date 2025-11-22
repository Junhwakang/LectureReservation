package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 예약 삭제 커맨드 (SFR-405, SFR-406)
 * 관리자가 예약을 삭제하고 취소 원인을 저장하는 작업을 캡슐화
 */
public class DeleteReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final String cancellationReason; // 취소 원인 (SFR-405, SFR-406)
    private final ReservationSubject subject;
    private final ReservationRepository repository;
    
    // Undo를 위한 삭제된 예약 저장
    private RoomReservation deletedReservation;
    
    public DeleteReservationCommand(String reservationId, String cancellationReason, ReservationSubject subject) {
        this.reservationId = reservationId;
        this.cancellationReason = cancellationReason;
        this.subject = subject;
        this.repository = ReservationRepository.getInstance();
    }
    
    @Override
    public BasicResponse execute() {
        try {
            // 삭제 전 예약 정보 저장 (Undo용)
            deletedReservation = repository.findById(reservationId);
            if (deletedReservation == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }
            
            // 취소 원인 저장 (SFR-406)
            deletedReservation.setCancellationReason(cancellationReason);
            
            // 실제 삭제
            boolean deleted = repository.deleteById(reservationId);
            if (!deleted) {
                return new BasicResponse("500", "예약 삭제에 실패했습니다.");
            }
            
            repository.saveToFile();
            
            // 옵저버에게 삭제 알림 (SFR-410: 관리자에 의한 예약 취소 시 알림)
            subject.notifyReservationCancelled(deletedReservation, cancellationReason);
            
            return new BasicResponse("200", "예약이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public BasicResponse undo() {
        if (deletedReservation == null) {
            return new BasicResponse("400", "Undo할 데이터가 없습니다.");
        }
        
        try {
            // 삭제된 예약 복원
            repository.save(deletedReservation);
            
            return new BasicResponse("200", "예약 삭제가 취소되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "Undo 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public String getDescription() {
        return "예약 삭제: ID=" + reservationId + ", 원인=" + cancellationReason;
    }
}
