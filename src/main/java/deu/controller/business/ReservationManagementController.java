package deu.controller.business;

import deu.command.*;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.observer.AdminNotificationObserver;
import deu.observer.LoggingObserver;
import deu.observer.NotificationObserver;
import deu.observer.ReservationSubject;
import deu.service.ReservationService;
import lombok.Getter;

/**
 * 예약 관리 컨트롤러 (리팩토링 버전)
 * 커맨드 패턴과 옵저버 패턴을 적용하여 구현
 */
public class ReservationManagementController {
    @Getter
    private static final ReservationManagementController instance = new ReservationManagementController();

    private ReservationManagementController() {
        // 사용자 알림용 옵저버 등록
        userNotificationSubject.attach(new NotificationObserver());
        userNotificationSubject.attach(new LoggingObserver());
        
        // 관리자 알림용 옵저버 등록 (새로 추가)
        adminNotificationSubject.attach(new AdminNotificationObserver());
        adminNotificationSubject.attach(new LoggingObserver());
        
        // ReservationService에 관리자 알림용 Subject 주입
        reservationService.setAdminNotificationSubject(adminNotificationSubject);
    }

    private final ReservationService reservationService = ReservationService.getInstance();
    private final ReservationCommandInvoker commandInvoker = new ReservationCommandInvoker();
    
    // 사용자에게 알림 (승인/거부/삭제 시)
    private final ReservationSubject userNotificationSubject = new ReservationSubject();
    
    // 관리자에게 알림 (새 예약 신청 시) - 새로 추가
    private final ReservationSubject adminNotificationSubject = new ReservationSubject();

    /**
     * 예약 수정 (커맨드 패턴 적용)
     * SFR-401, SFR-402, SFR-407
     */
    public BasicResponse handleModifyRoomReservation(RoomReservationRequest payload) {
        ReservationCommand command = new ModifyReservationCommand(payload, userNotificationSubject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 관리자 예약 삭제 (커맨드 패턴 + 옵저버 패턴 적용)
     * SFR-405, SFR-406: 취소 원인 등록 및 알림 전달
     */
    public BasicResponse handleDeleteRoomReservation(String payload) {
        return handleDeleteRoomReservationWithReason(payload, "관리자에 의한 예약 삭제");
    }

    /**
     * 관리자 예약 삭제 (취소 원인 포함)
     * SFR-405: 취소 원인 등록
     */
    public BasicResponse handleDeleteRoomReservationWithReason(String reservationId, String cancellationReason) {
        ReservationCommand command = new DeleteReservationCommand(reservationId, cancellationReason, userNotificationSubject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 예약 상태 변경 "대기 -> 승인" (커맨드 패턴 + 옵저버 패턴 적용)
     * SFR-409, SFR-410: 승인 시 사용자에게 알림 전달
     */
    public BasicResponse handleChangeRoomReservationStatus(String payload) {
        ReservationCommand command = new ApproveReservationCommand(payload, userNotificationSubject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 예약 거부 (커맨드 패턴 + 옵저버 패턴 적용)
     * SFR-410, SFR-411, SFR-412: 거부 시 사용자에게 알림 전달
     */
    public BasicResponse handleRejectRoomReservation(String reservationId, String rejectionReason) {
        ReservationCommand command = new RejectReservationCommand(reservationId, rejectionReason, userNotificationSubject);
        return commandInvoker.executeCommand(command);
    }

    /**
     * 예약 상태가 "대기" 인 모든 예약 내역 반환
     */
    public BasicResponse handleFindAllRoomReservation() {
        return reservationService.findAllRoomReservation();
    }

    /**
     * 마지막 작업 취소 (Undo)
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
     * 커맨드 히스토리 초기화
     */
    public void clearCommandHistory() {
        commandInvoker.clearHistory();
    }

    /**
     * 사용자 알림 Subject 반환 (테스트용)
     */
    public ReservationSubject getUserNotificationSubject() {
        return userNotificationSubject;
    }
    
    /**
     * 관리자 알림 Subject 반환 (테스트용)
     */
    public ReservationSubject getAdminNotificationSubject() {
        return adminNotificationSubject;
    }
}
