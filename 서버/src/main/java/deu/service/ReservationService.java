package deu.service;

import deu.model.dto.request.data.reservation.DeleteRoomReservationRequest;
import deu.model.dto.request.data.reservation.RoomReservationLocationRequest;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import deu.model.dto.response.BasicResponse;
import deu.model.enums.ReservationPurpose;
import deu.observer.LoggingObserver;
import deu.observer.NotificationObserver;
import deu.observer.ReservationSubject;
import deu.service.reservation.validation.AdvanceValidationStrategy;
import deu.service.reservation.validation.CapacityLimitStrategy;
import lombok.Getter;
import deu.service.reservation.validation.ReservationValidator;
import deu.service.reservation.validation.DuplicateReservationValidationStrategy;
import deu.service.reservation.validation.ProfFirstStrategy;
import deu.service.reservation.validation.PurposeMaxValidationStrategy;
import deu.service.reservation.validation.WeeklyLimitReservationValidationStrategy;
import deu.service.reservation.validation.ReservationValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationService {

    // 싱글톤 인스턴스
    @Getter
    private static final ReservationService instance = new ReservationService();

    private final ReservationValidator reservationValidator;

    // 마지막으로 생성된 예약 (옵저버 알림용)
    private RoomReservation lastCreatedReservation;

    private ReservationService() {
        this.reservationValidator = new ReservationValidator()
                .addStrategy(new DuplicateReservationValidationStrategy()) // SFR-203 전략패턴 
                .addStrategy(new WeeklyLimitReservationValidationStrategy())
                .addStrategy(new PurposeMaxValidationStrategy()) // SFR-212 검증 전략패턴
                .addStrategy(new AdvanceValidationStrategy()) // SFR-214 검증 전략
                .addStrategy(new ProfFirstStrategy()) // SFR-216 검증
                .addStrategy(new CapacityLimitStrategy());

        // 옵저버 등록
        ReservationSubject subject = ReservationSubject.getInstance();
        subject.attach(new LoggingObserver());
        subject.attach(new NotificationObserver());
    }

    /**
     * 마지막으로 생성된 예약 반환 (옵저버 알림용)
     */
    public RoomReservation getLastCreatedReservation() {
        return lastCreatedReservation;
    }

    // 사용자 관점 ========================================================================================================
    // 예약 신청 // SFR-203 중복예약
    public BasicResponse createRoomReservation(RoomReservationRequest payload) {
        try {
            // RoomReservation 엔티티 생성
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setBuildingName(payload.getBuildingName());
            roomReservation.setFloor(payload.getFloor());
            roomReservation.setLectureRoom(payload.getLectureRoom());
            roomReservation.setNumber(payload.getNumber());
            roomReservation.setTitle(payload.getTitle());
            roomReservation.setDescription(payload.getDescription());
            roomReservation.setDate(payload.getDate());
            roomReservation.setDayOfTheWeek(payload.getDayOfTheWeek());
            roomReservation.setStartTime(payload.getStartTime());
            roomReservation.setEndTime(payload.getEndTime());

            //SFR-211, 213, 215,217 목적, 인원수, 수용인원
            roomReservation.setPurpose(payload.getPurpose());
            roomReservation.setParticipantCount(payload.getParticipantCount());
            roomReservation.setCapacity(payload.getCapacity());

            //Repo 조회
            ReservationRepository repo = ReservationRepository.getInstance();
            List<RoomReservation> userReservations = repo.findByUser(payload.getNumber());

            boolean professor = isProfessor(payload.getNumber());

            if (!professor) {
                // SFR-203 Strategy 기반 중복 예약 검증
                reservationValidator.validate(payload, repo, userReservations);
            } else {
                // === 교수: SFR-210 – 같은 시간대 기존 예약들 자동 취소 ===
                cancelConflictsForProfessor(roomReservation, repo);
                // 교수는 강의/보강이 우선이기 때문에 중복 검사로 막지 않는다
            }
            // 최종 저장
            repo.save(roomReservation);

            // 마지막 생성 예약 저장 (옵저버 알림용)
            this.lastCreatedReservation = roomReservation;

            return new BasicResponse("200", "예약이 완료되었습니다.");

        } catch (ReservationValidationException e) {
            // 전략에서 던진 예외 → 적절한 코드로 매핑
            return new BasicResponse("409", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "서버 오류: " + e.getMessage());
        }
    }

    // 개인별 예약 삭제 TODO: number 와 id에 해당하는 RoomReservation 의 number가 동일하면 삭제 / 다르면 비정상적인 접근 처리
    public BasicResponse deleteRoomReservationFromUser(DeleteRoomReservationRequest payload) {
        RoomReservation target = ReservationRepository.getInstance().findById(payload.roomReservationId);

        if (target == null) {
            return new BasicResponse("404", "예약을 찾을 수 없습니다.");
        }

        if (!target.getNumber().equals(payload.number)) {
            return new BasicResponse("403", "본인의 예약만 삭제할 수 있습니다.");
        }

        ReservationRepository.getInstance().deleteById(payload.roomReservationId);
        return new BasicResponse("200", "예약이 삭제되었습니다.");
    }

    // 개인별 주간 예약 조회 반환: 7x13 배열 (당일 ~ +6일) TODO: RoomReservation[7][13]
    public BasicResponse weekRoomReservationByUserNumber(String payload) {
        RoomReservation[][] schedule = new RoomReservation[7][13];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        List<RoomReservation> reservations = ReservationRepository.getInstance().findByUser(payload).stream()
                .filter(r -> {
                    try {
                        LocalDate date = LocalDate.parse(r.getDate(), formatter);
                        return !date.isBefore(today) && !date.isAfter(today.plusDays(6));
                    } catch (Exception e) {
                        return false;
                    }
                }).toList();

        for (RoomReservation r : reservations) {
            try {
                int dayIndex = (int) ChronoUnit.DAYS.between(today, LocalDate.parse(r.getDate(), formatter));
                int periodIndex = Integer.parseInt(r.getStartTime().split(":")[0]) - 9;
                if (dayIndex >= 0 && dayIndex < 7 && periodIndex >= 0 && periodIndex < 13) {
                    schedule[dayIndex][periodIndex] = r;
                }
            } catch (Exception ignored) {
            }
        }

        return new BasicResponse("200", schedule);
    }

    // 사용자별 예약 리스트 조회 TODO: 동일하게 당일 + 6 일 뒤의정보를 반환해야 한다.
    public BasicResponse getReservationsByUser(String payload) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(6); // 오늘부터 6일 후까지 포함

        List<RoomReservation> reservations = ReservationRepository.getInstance()
                .findByUser(payload).stream()
                .filter(r -> {
                    try {
                        LocalDate date = LocalDate.parse(r.getDate(), formatter);
                        return !date.isBefore(today) && !date.isAfter(endDate); // today ≤ date ≤ today+6
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();

        return new BasicResponse("200", reservations);
    }

    // 통합 관점 ==========================================================================================================
    // 예약 수정        // 기존 코드이며 SFR-209
    public BasicResponse modifyRoomReservation(RoomReservationRequest payload) {
        try {
            ReservationRepository repo = ReservationRepository.getInstance();

            RoomReservation original = repo.findById(payload.getId());
            if (original == null) {
                return new BasicResponse("404", "예약을 찾을 수 없습니다.");
            }

            // 필드 업데이트    // SFR-209 기능
            original.setBuildingName(payload.getBuildingName());
            original.setFloor(payload.getFloor());
            original.setLectureRoom(payload.getLectureRoom());
            original.setTitle(payload.getTitle());
            original.setDescription(payload.getDescription());
            original.setDate(payload.getDate());
            original.setDayOfTheWeek(payload.getDayOfTheWeek());
            original.setStartTime(payload.getStartTime());
            original.setEndTime(payload.getEndTime());

            //SFR-211, 213, 215,217 목적, 인원수, 수용인원 
            original.setPurpose(payload.getPurpose());  // 목적
            original.setParticipantCount(payload.getParticipantCount());    //인원 수
            original.setCapacity(payload.getCapacity());    // 수용 인원

            // 파일 저장
            repo.saveToFile();

            return new BasicResponse("200", "예약이 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "예약 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 건물 강의실별 주간 예약 조회 반환: 7x13 배열 (당일 +6일 까지) TODO: RoomReservation[7][13]
    public BasicResponse weekRoomReservationByLectureroom(RoomReservationLocationRequest payload) {
        RoomReservation[][] schedule = new RoomReservation[7][13];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        List<RoomReservation> reservations = ReservationRepository.getInstance().findAll().stream()
                .filter(r -> r.getBuildingName().equals(payload.building)
                && r.getFloor().equals(payload.floor)
                && r.getLectureRoom().equals(payload.lectureroom))
                .filter(r -> {
                    try {
                        LocalDate date = LocalDate.parse(r.getDate(), formatter);
                        return !date.isBefore(today) && !date.isAfter(today.plusDays(6));
                    } catch (Exception e) {
                        return false;
                    }
                }).toList();

        for (RoomReservation r : reservations) {
            try {
                int dayIndex = (int) ChronoUnit.DAYS.between(today, LocalDate.parse(r.getDate(), formatter));
                int periodIndex = Integer.parseInt(r.getStartTime().split(":")[0]) - 9;
                if (dayIndex >= 0 && dayIndex < 7 && periodIndex >= 0 && periodIndex < 13) {
                    schedule[dayIndex][periodIndex] = r;
                }
            } catch (Exception ignored) {
            }
        }

        return new BasicResponse("200", schedule);
    }

    // 관리자 관점 ========================================================================================================
    // 관리자 예약 삭제
    public BasicResponse deleteRoomReservationFromManagement(String payload) {
        boolean deleted = ReservationRepository.getInstance().deleteById(payload);
        if (deleted) {
            ReservationRepository.getInstance().saveToFile();
            return new BasicResponse("200", "예약이 삭제되었습니다.");
        }
        return new BasicResponse("404", "예약을 찾을 수 없습니다.");
    }

    // 예약 상태 변경 "대기 -> 완료"
    public BasicResponse changeRoomReservationStatus(String payload) {
        RoomReservation target = ReservationRepository.getInstance().findById(payload);
        if (target == null) {
            return new BasicResponse("404", "예약을 찾을 수 없습니다.");
        }

        target.setStatus("승인");
        ReservationRepository.getInstance().saveToFile();
        return new BasicResponse("200", "예약 상태가 승인로 변경되었습니다.");
    }

    // 예약 상태가 "대기" 인 모든 예약 내역 반환
    public BasicResponse findAllRoomReservation() {
        List<RoomReservation> result = ReservationRepository.getInstance().findAll().stream()
                .filter(r -> "대기".equals(r.getStatus()))
                .toList();

        return new BasicResponse("200", result);
    }
    // =================================================================================================================

    // 교수 우선순위로 취소 메서드
    private boolean isProfessor(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }
        char c = number.charAt(0);
        return c == 'p' || c == 'P';
    }

    // SFR-210 교수가 예약할 경우 동일 시간대 취소 가능 메서드
    // SFR-218 보강/세미나로 인한 예약으로 취소 될 경우 취소 사실 알림.
    private void cancelConflictsForProfessor(RoomReservation newReservation,
            ReservationRepository repo) {

        String date = newReservation.getDate();
        String building = newReservation.getBuildingName();
        String floor = newReservation.getFloor();
        String room = newReservation.getLectureRoom();
        String startTime = newReservation.getStartTime();

        // 같은 날짜 + 같은 강의실 + 같은 시작시간 예약들 찾기
        List<RoomReservation> conflicts = repo.findAll().stream()
                .filter(r -> date.equals(r.getDate()))
                .filter(r -> building.equals(r.getBuildingName()))
                .filter(r -> floor.equals(r.getFloor()))
                .filter(r -> room.equals(r.getLectureRoom()))
                .filter(r -> startTime.equals(r.getStartTime()))
                .toList();

        ReservationPurpose purpose = null;
        try {
            if (newReservation.getPurpose() != null) {
                purpose = ReservationPurpose.fromLabel(newReservation.getPurpose());
            }
        } catch (IllegalArgumentException ignored) {
            // label이 enum과 안 맞으면 그냥 null
        }

        
        // 보강 or 세미나인지 확인
        boolean isSupplyOrSeminar = 
                purpose == ReservationPurpose.SUPPLEMENT ||
                purpose == ReservationPurpose.SEMINAR;
        
        String reasonForCancel;
        if(isSupplyOrSeminar){
            //SFR-218 : 보강 / 세미나로 인한 알림 전송
            reasonForCancel = "보강 및 세미나의 이유로 기존 예약이 취소되었습니다.";
        }else{
            // 일반적인 이유
            reasonForCancel = "교수 예약으로 자동 취소 되었습니다.";
        }
        
        ReservationSubject subject = ReservationSubject.getInstance();
        
        for (RoomReservation conflict : conflicts) {
            // 상태를 취소로 바꾸고, 취소 사유 남기기
            conflict.setStatus("취소");
            conflict.setCancellationReason(reasonForCancel);
            subject.notifyReservationCancelled(conflict, reasonForCancel);
        }

        // 변경사항 파일에 반영
        repo.saveToFile();
    }
}
