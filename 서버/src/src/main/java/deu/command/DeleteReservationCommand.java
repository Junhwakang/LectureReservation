package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 커맨드 패턴 - 예약 삭제 커맨드
 * SFR-405, SFR-406: 관리자가 예약을 삭제하고 취소 원인을 등록하는 기능
 */
public class DeleteReservationCommand implements ReservationCommand {
    private final String reservationId;
    private final String cancellationReason; // SFR-405: 취소 원인
    private final ReservationRepository repository;
    private final ReservationSubject subject;
    private RoomReservation deletedReservation; // undo를 위한 삭제된 예약 저장

    public DeleteReservationCommand(String reservationId, String cancellationReason, ReservationSubject subject) {
        this.reservationId = reservationId;
        this.cancellationReason = cancellationReason;
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

            // 삭제 전 예약 정보 저장 (undo를 위해)
            deletedReservation = copyReservation(target);

            // 예약 삭제
            boolean deleted = repository.deleteById(reservationId);
            if (deleted) {
                repository.saveToFile();
                
                // 옵저버에게 알림 (SFR-405: 취소 원인 포함)
                String message = "예약이 삭제되었습니다.";
                if (cancellationReason != null && !cancellationReason.trim().isEmpty()) {
                    message += " [취소 원인: " + cancellationReason + "]";
                }
                subject.notifyObservers(deletedReservation, message);
                
                return new BasicResponse("200", "예약이 삭제되었습니다.");
            }
            return new BasicResponse("500", "예약 삭제에 실패했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public BasicResponse undo() {
        if (deletedReservation == null) {
            return new BasicResponse("400", "복구할 예약이 없습니다.");
        }

        try {
            repository.save(deletedReservation);
            return new BasicResponse("200", "예약 삭제가 취소되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private RoomReservation copyReservation(RoomReservation source) {
        RoomReservation copy = new RoomReservation();
        copy.setId(source.getId());
        copy.setBuildingName(source.getBuildingName());
        copy.setFloor(source.getFloor());
        copy.setLectureRoom(source.getLectureRoom());
        copy.setNumber(source.getNumber());
        copy.setStatus(source.getStatus());
        copy.setTitle(source.getTitle());
        copy.setDescription(source.getDescription());
        copy.setDate(source.getDate());
        copy.setDayOfTheWeek(source.getDayOfTheWeek());
        copy.setStartTime(source.getStartTime());
        copy.setEndTime(source.getEndTime());
        return copy;
    }
}
