package deu.controller.business;

import deu.command.*;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.observer.LoggingObserver;
import deu.observer.NotificationObserver;
import deu.observer.ReservationSubject;
import deu.service.ReservationService;
import lombok.Getter;

/**
 * 예약 관리 컨트롤러
 * 커맨드 패턴과 옵저버 패턴을 적용하여 리팩토링됨
 * 
 * 적용된 요구사항:
 * - SFR-401~402: 예약 수정 (커맨드 패턴)
 * - SFR-403~404: 예약 승인/거부 (커맨드 패턴)
 * - SFR-405~406: 예약 삭제 with 취소 원인 (커맨드 패턴)
 * - SFR-409~410: 알림 전달 (옵저버 패턴)
 * - SFR-411~412: 변경 이력 로깅 (옵저버 패턴)
 */
public class ReservationManagementController {
    @Getter
    private static final ReservationManagementController instance = new ReservationManagementController();

    private final ReservationService reservationService = ReservationService.getInstance();
    private final ReservationCommandInvoker commandInvoker = ReservationCommandInvoker.getInstance();
    private final ReservationSubject subject = ReservationSubject.getInstance();

    private ReservationManagementController() {
        // 옵저버 등록 (SFR-409, SFR-410, SFR-412)
        subject.attach(new NotificationObserver()); // 사용자 알림
        subject.attach(new LoggingObserver());       // 변경 이력 로그
    }

    /**
     * 예약 수정 (SFR-401, SFR-402)
     * 커맨드 패턴 적용
     */
    public BasicResponse handleModifyRoomReservation(RoomReservationRequest payload) {
        ReservationCommand command = new ModifyReservationCommand(payload, subject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 관리자 예약 삭제 (SFR-405, SFR-406)
     * 취소 원인과 함께 삭제
     */
    public BasicResponse handleDeleteRoomReservation(String reservationId, String cancellationReason) {
        ReservationCommand command = new DeleteReservationCommand(reservationId, cancellationReason, subject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 관리자 예약 삭제 (취소 원인 없이) - 하위 호환성 유지
     */
    public BasicResponse handleDeleteRoomReservation(String reservationId) {
        return handleDeleteRoomReservation(reservationId, "관리자에 의한 삭제");
    }

    /**
     * 예약 승인 (SFR-403, SFR-404, SFR-409)
     * 커맨드 패턴 + 옵저버 패턴 적용
     */
    public BasicResponse handleApproveRoomReservation(String reservationId) {
        ReservationCommand command = new ApproveReservationCommand(reservationId, subject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 예약 거부 (SFR-403, SFR-404, SFR-409)
     * 거부 원인과 함께 거부
     */
    public BasicResponse handleRejectRoomReservation(String reservationId, String rejectionReason) {
        ReservationCommand command = new RejectReservationCommand(reservationId, rejectionReason, subject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 예약 상태 변경 "대기 -> 승인" (기존 메서드 - 하위 호환성 유지)
     * 내부적으로 handleApproveRoomReservation 호출
     */
    public BasicResponse handleChangeRoomReservationStatus(String reservationId) {
        return handleApproveRoomReservation(reservationId);
    }

    /**
     * 예약 상태가 "대기" 인 모든 예약 내역 반환 (SFR-411)
     */
    public BasicResponse handleFindAllRoomReservation() {
        return reservationService.findAllRoomReservation();
    }

    /**
     * 마지막 작업 실행 취소 (Undo)
     */
    public BasicResponse handleUndo() {
        return commandInvoker.undo();
    }

    /**
     * 취소한 작업 재실행 (Redo)
     */
    public BasicResponse handleRedo() {
        return commandInvoker.redo();
    }

    /**
     * 커맨드 히스토리 조회
     */
    public String getCommandHistory() {
        return commandInvoker.getCommandHistory();
    }
}
