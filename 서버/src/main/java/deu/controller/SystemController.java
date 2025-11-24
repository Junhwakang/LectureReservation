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
    // ⭐ Singleton 패턴: AccountManagerController 인스턴스 사용
    private final AccountManagerController accountManagerController = AccountManagerController.getInstance();
    // UserManagementInvoker는 Singleton으로 가져옴
    private final UserManagementInvoker userManagementInvoker = UserManagementInvoker.getInstance();
    private final LectureController lectureController = LectureController.getInstance();
    private final ReservationController reservationController = ReservationController.getInstance();
    // ⭐ 지연 초기화: ReservationManagementController는 예약 관리 요청이 올 때만 초기화
    private ReservationManagementController reservationManagementController;

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
                System.out.println("[SystemController] UserCommandRequest 수신: command='" + r.command + "'");
                return switch (r.command) {
                    case "로그인" ->
                        userController.handleLogin((LoginRequest) r.payload);
                    case "회원가입" ->
                        userController.handleSignup((SignupRequest) r.payload);
                    case "로그아웃" ->
                        userController.handleLogout((LogoutRequest) r.payload);
                    case "동시접속자" ->
                        userController.handleCurrentUser();
                    case "사용자 이름 반환" ->
                        userController.handleFindUserName((FindUserNameRequest) r.payload);
                    // ⭐ Singleton 패턴: AccountManager 명령 처리
                    case "AccountManager로그인" -> {
                        System.out.println("[SystemController] AccountManager로그인 처리 시작");
                        yield accountManagerController.handleLogin((LoginRequest) r.payload);
                    }
                    case "AccountManager회원가입" -> {
                        System.out.println("[SystemController] AccountManager회원가입 처리 시작");
                        yield accountManagerController.handleSignup((SignupRequest) r.payload);
                    }
                    case "AccountManager로그아웃" ->
                        accountManagerController.handleLogout((LogoutRequest) r.payload);
                    case "AccountManager역할조회" ->
                        accountManagerController.handleGetRole((String) r.payload);
                    default -> {
                        System.out.println("[SystemController] 알 수 없는 명령어: '" + r.command + "'");
                        yield new BasicResponse("404", "알 수 없는 명령어");
                    }
                };
            } // ❌ 기존 UserManagementController 및 UserManagementCommandRequest 관련 로직은 Command 패턴으로 대체되어 이 위치에서 제거됩니다.
            // 예약 컨트롤러 (기존 방식 유지)
            // 예약 컨트롤러 (기존 방식 유지)
            else if (request instanceof ReservationCommandRequest r) {
                // ★ 디버그 로그 추가
                System.out.println("[SystemController] ReservationCommandRequest 수신: command='" + r.command + "'");
                System.out.println("[SystemController] payload 타입: "
                        + (r.payload == null ? "null" : r.payload.getClass().getName()));

                try {
                    return switch (r.command) {
                        case "예약 요청" -> {
                            System.out.println("[SystemController] → 예약 요청 처리 시작");
                            yield reservationController.handleAddRoomReservation((RoomReservationRequest) r.payload);
                        }
                        case "예약 수정" -> {
                            System.out.println("[SystemController] → 예약 수정 처리 시작");
                            yield reservationController.handleModifyRoomReservation((RoomReservationRequest) r.payload);
                        }
                        case "예약 삭제" -> {
                            System.out.println("[SystemController] → 예약 삭제 처리 시작");
                            yield reservationController.handlDeleteRoomReservation((DeleteRoomReservationRequest) r.payload);
                        }
                        case "사용자 예약 리스트 조회" ->
                            reservationController.handleUserRoomReservationList((String) r.payload);
                        case "사용자 예약 배열 조회" ->
                            reservationController.handleWeekRoomReservationByUserNumber((String) r.payload);
                        case "강의실 예약 배열 조회" ->
                            reservationController.handleWeekRoomReservationByLectureroom((RoomReservationLocationRequest) r.payload);
                        default -> {
                            System.out.println("[SystemController] 알 수 없는 예약 명령어: '" + r.command + "'");
                            yield new BasicResponse("404", "알 수 없는 명령어");
                        }
                    };
                } catch (Exception e) {
                    // ★ 예약 쪽에서 터지는 예외를 명확히
                    e.printStackTrace();
                    return new BasicResponse("500", "예약 처리 중 예외: " + e.toString());
                }
            } // 강의 컨트롤러 (기존 방식 유지)
            else if (request instanceof LectureCommandRequest r) {
                return switch (r.command) {
                    case "주간 강의 조회" ->
                        lectureController.handleReturnLectureOfWeek((LectureRequest) r.payload);
                    default ->
                        new BasicResponse("404", "알 수 없는 명령어");
                };
            }

            return new BasicResponse("405", "지원하지 않는 요청 타입");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse("500", "서버 처리 중 예외 발생: " + e.toString()); // getMessage() 말고 toString()
        }
    }
}
