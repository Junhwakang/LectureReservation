package deu.command;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;

/**
 * 예약 수정 커맨드 (SFR-401, SFR-402)
 * 관리자가 예약 정보를 수정하는 작업을 캡슐화
 */
public class ModifyReservationCommand implements ReservationCommand {
    private final RoomReservationRequest request;
    private final ReservationSubject subject;
    private final ReservationRepository repository;
    
    // Undo를 위한 원본 데이터 저장
    private RoomReservation originalReservation;
    
    public ModifyReservationCommand(RoomReservationRequest request, ReservationSubject subject) {
        this.request = request;
        this.subject = subject;
        this.repository = ReservationRepository.getInstance();
    }
    
    @Override
    public BasicResponse execute() {
        try {
            // 원본 예약 찾기
            RoomReservation original = repository.findById(request.getId());
            if (original == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }
            
            // Undo를 위해 원본 저장 (Deep Copy)
            originalReservation = copyReservation(original);
            
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
            
            // 옵저버에게 변경 알림 (SFR-412: 예약 변경 이력)
            subject.notifyReservationModified(original);
            
            return new BasicResponse("200", "예약이 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public BasicResponse undo() {
        if (originalReservation == null) {
            return new BasicResponse("400", "Undo할 데이터가 없습니다.");
        }
        
        try {
            RoomReservation current = repository.findById(originalReservation.getId());
            if (current == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }
            
            // 원본 데이터로 복원
            current.setBuildingName(originalReservation.getBuildingName());
            current.setFloor(originalReservation.getFloor());
            current.setLectureRoom(originalReservation.getLectureRoom());
            current.setTitle(originalReservation.getTitle());
            current.setDescription(originalReservation.getDescription());
            current.setDate(originalReservation.getDate());
            current.setDayOfTheWeek(originalReservation.getDayOfTheWeek());
            current.setStartTime(originalReservation.getStartTime());
            current.setEndTime(originalReservation.getEndTime());
            current.setStatus(originalReservation.getStatus());
            
            repository.saveToFile();
            
            return new BasicResponse("200", "예약 수정이 취소되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "Undo 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public String getDescription() {
        return "예약 수정: ID=" + request.getId();
    }
    
    // Deep Copy 헬퍼 메서드
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
