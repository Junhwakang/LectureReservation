package deu.command;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 커맨드 패턴 - 예약 수정 커맨드
 * SFR-401, SFR-402: 관리자가 예약 정보를 수정하는 기능
 */
public class ModifyReservationCommand implements ReservationCommand {
    private final RoomReservationRequest request;
    private final ReservationRepository repository;
    private final ReservationSubject subject;
    private RoomReservation previousState; // undo를 위한 이전 상태 저장

    public ModifyReservationCommand(RoomReservationRequest request, ReservationSubject subject) {
        this.request = request;
        this.repository = ReservationRepository.getInstance();
        this.subject = subject;
    }

    @Override
    public BasicResponse execute() {
        try {
            RoomReservation original = repository.findById(request.getId());
            if (original == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }

            // 이전 상태 저장 (undo를 위해)
            previousState = copyReservation(original);

            // 필드 업데이트
            original.setBuildingName(request.getBuildingName());
            original.setFloor(request.getFloor());
            original.setLectureRoom(request.getLectureRoom());
            original.setTitle(request.getTitle());
            original.setDescription(request.getDescription());
            original.setDate(request.getDate());
            original.setDayOfTheWeek(request.getDayOfTheWeek());
            original.setStartTime(request.getStartTime());
            original.setEndTime(request.getEndTime());

            // 파일 저장
            repository.saveToFile();

            // 옵저버에게 알림 (SFR-407: 관리자가 예약을 수정할 경우 알림)
            subject.notifyObservers(original, "예약이 수정되었습니다.");

            return new BasicResponse("200", "예약이 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public BasicResponse undo() {
        if (previousState == null) {
            return new BasicResponse("400", "되돌릴 이전 상태가 없습니다.");
        }

        try {
            RoomReservation current = repository.findById(request.getId());
            if (current != null) {
                // 이전 상태로 복원
                current.setBuildingName(previousState.getBuildingName());
                current.setFloor(previousState.getFloor());
                current.setLectureRoom(previousState.getLectureRoom());
                current.setTitle(previousState.getTitle());
                current.setDescription(previousState.getDescription());
                current.setDate(previousState.getDate());
                current.setDayOfTheWeek(previousState.getDayOfTheWeek());
                current.setStartTime(previousState.getStartTime());
                current.setEndTime(previousState.getEndTime());

                repository.saveToFile();
                return new BasicResponse("200", "예약 수정이 취소되었습니다.");
            }
            return new BasicResponse("404", "예약을 찾을 수 없습니다.");
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
