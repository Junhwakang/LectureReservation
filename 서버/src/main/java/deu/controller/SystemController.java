// 경로: 서버/src/main/java/deu/controller/SystemController.java
package deu.controller;

import deu.command.UserCommand; // ⭐ deu.command.UserCommand 대신 Command 인터페이스 사용
import deu.controller.business.*;
import deu.model.dto.request.command.*;
import deu.model.dto.request.data.lecture.LectureRequest;
import deu.model.dto.request.data.reservation.DeleteRoomReservationRequest;
import deu.model.dto.request.data.reservation.RoomReservationLocationRequest;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.request.data.user.*;
import deu.model.dto.response.BasicResponse;

public class SystemController {
    private final UserController userController = UserController.getInstance();
    // UserManagementInvoker는 Singleton으로 가져옴
    private final UserManagementInvoker userManagementInvoker = UserManagementInvoker.getInstance(); 
    private final LectureController lectureController = LectureController.getInstance();
    private final ReservationController reservationController = ReservationController.getInstance();
    private final ReservationManagementController reservationManagementController = ReservationManagementController.getInstance();

    public Object handle(Object request) {
        try {
            // ⭐ 커맨드 패턴 적용: Command 인터페이스 확인
            if (request instanceof UserCommand command) { // 기존 UserCommand/UserManagementCommandRequest 로직 대체
                System.out.println("[SystemController] Command 타입 확인 완료");
                System.out.println("[SystemController] 커맨드: " + command.getClass().getSimpleName());
                // Invoker에게 전달 (더 이상 switch 문 없음!)
                return userManagementInvoker.executeCommand(command);
            }
            
            // 사용자 컨트롤러 (기존 방식 유지)
            if (request instanceof UserCommandRequest r) {
                return switch (r.command) {
                    case "로그인" -> userController.handleLogin((LoginRequest) r.payload);
                    case "회원가입" -> userController.handleSignup((SignupRequest) r.payload);
                    case "로그아웃" -> userController.handleLogout((LogoutRequest) r.payload);
                    case "동시접속자" -> userController.handleCurrentUser();
                    case "사용자 이름 반환" -> userController.handleFindUserName((FindUserNameRequest) r.payload);
                    default -> new BasicResponse("404", "알 수 없는 명령어");
                };
            }

            // ❌ 기존 UserManagementController 및 UserManagementCommandRequest 관련 로직은 Command 패턴으로 대체되어 이 위치에서 제거됩니다.

            // 예약 컨트롤러 (기존 방식 유지)
            else if (request instanceof ReservationCommandRequest r) {
                return switch (r.command) {
                    case "예약 요청" -> reservationController.handleAddRoomReservation((RoomReservationRequest) r.payload);
                    case "예약 수정" -> reservationController.handleModifyRoomReservation((RoomReservationRequest) r.payload);
                    case "예약 삭제" -> reservationController.handlDeleteRoomReservation((DeleteRoomReservationRequest) r.payload);
                    case "사용자 예약 리스트 조회" -> reservationController.handleUserRoomReservationList((String) r.payload);
                    case "사용자 예약 배열 조회" -> reservationController.handleWeekRoomReservationByUserNumber((String) r.payload);
                    case "강의실 예약 배열 조회" -> reservationController.handleWeekRoomReservationByLectureroom((RoomReservationLocationRequest) r.payload);
                    default -> new BasicResponse("404", "알 수 없는 명령어");
                };
            }

            // 예약 관리 컨트롤러 (기존 방식 유지)
            else if (request instanceof ReservationManagementCommandRequest r) {
                return switch (r.command) {
                    case "예약 수정" -> reservationManagementController.handleModifyRoomReservation((RoomReservationRequest) r.payload);
                    case "예약 삭제" -> reservationManagementController.handleDeleteRoomReservation((String) r.payload);
                    case "예약 대기 전체 조회" -> reservationManagementController.handleFindAllRoomReservation();
                    case "예약 상태 변경" -> reservationManagementController.handleChangeRoomReservationStatus((String) r.payload);
                    default -> new BasicResponse("404", "알 수 없는 명령어");
                };
            }

            // 강의 컨트롤러 (기존 방식 유지)
            else if (request instanceof LectureCommandRequest r) {
                return switch (r.command) {
                    case "주간 강의 조회" -> lectureController.handleReturnLectureOfWeek((LectureRequest) r.payload);
                    default -> new BasicResponse("404", "알 수 없는 명령어");
                };
            }

            return new BasicResponse("405", "지원하지 않는 요청 타입");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "서버 처리 중 예외 발생: " + e.getMessage());
        }
    }
}