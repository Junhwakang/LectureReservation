// 경로: 서버/src/main/java/deu/controller/SystemController.java
package deu.controller;

import deu.command.UserCommand;
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
    private final AccountManagerController accountManagerController = AccountManagerController.getInstance();
    private final UserManagementInvoker userManagementInvoker = UserManagementInvoker.getInstance();
    private final LectureController lectureController = LectureController.getInstance();
    private final ReservationController reservationController = ReservationController.getInstance();
    
    // [수정] ReservationManagementController 초기화 (기존에는 null로 선언만 되어 있었음)
    private final ReservationManagementController reservationManagementController = ReservationManagementController.getInstance();

    public Object handle(Object request) {
        try {
            // 커맨드 패턴 적용: Command 인터페이스 확인 (사용자 관리용)
            if (request instanceof UserCommand command) { 
                System.out.println("[SystemController] Command 타입 확인 완료");
                System.out.println("[SystemController] 커맨드: " + command.getClass().getSimpleName());
                return userManagementInvoker.executeCommand(command);
            }

            // 사용자 컨트롤러 (기존 방식 유지)
            if (request instanceof UserCommandRequest r) {
                //System.out.println("[SystemController] UserCommandRequest 수신: command='" + r.command + "'");
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
                    
                    // AccountManager 명령 처리
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
            }
            
            // 예약 컨트롤러 (일반 사용자 예약 관련)
            else if (request instanceof ReservationCommandRequest r) {
               // System.out.println("[SystemController] ReservationCommandRequest 수신: command='" + r.command + "'");
                //System.out.println("[SystemController] payload 타입: "
                  //      + (r.payload == null ? "null" : r.payload.getClass().getName()));

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
                    e.printStackTrace();
                    return new BasicResponse("500", "예약 처리 중 예외: " + e.toString());
                }
            }
            
            // [추가] 관리자용 예약 관리 컨트롤러 (ReservationManagementController)
            else if (request instanceof ReservationManagementCommandRequest r) {
                System.out.println("[SystemController] ReservationManagementCommandRequest 수신: command='" + r.command + "'");
                
                return switch (r.command) {
                    case "예약 대기 전체 조회" -> 
                        reservationManagementController.handleFindAllRoomReservation();
                        
                    case "예약 승인" -> 
                        reservationManagementController.handleApproveRoomReservation((String) r.payload);
                        
                    case "예약 거부" -> {
                        String[] args = (String[]) r.payload;
                        yield reservationManagementController.handleRejectRoomReservation(args[0], args[1]);
                    }
                    
                    case "예약 삭제" -> {
                        // 취소 사유가 있는 경우(String[])와 없는 경우(String) 분기 처리
                        if (r.payload instanceof String[]) {
                            String[] args = (String[]) r.payload;
                            yield reservationManagementController.handleDeleteRoomReservation(args[0], args[1]);
                        } else {
                            yield reservationManagementController.handleDeleteRoomReservation((String) r.payload);
                        }
                    }
                    
                    case "예약 수정" -> 
                        reservationManagementController.handleModifyRoomReservation((RoomReservationRequest) r.payload);
                        
                    default -> {
                        System.out.println("[SystemController] 알 수 없는 예약 관리 명령어: '" + r.command + "'");
                        yield new BasicResponse("404", "알 수 없는 명령어");
                    }
                };
            }
            
            // 강의 컨트롤러
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
            return new BasicResponse("500", "서버 처리 중 예외 발생: " + e.toString());
        }
    }
}