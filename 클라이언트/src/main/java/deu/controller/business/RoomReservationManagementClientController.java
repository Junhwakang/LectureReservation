package deu.controller.business;

import deu.config.Config;
import deu.config.ConfigLoader;
import deu.model.dto.request.command.ReservationManagementCommandRequest;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RoomReservationManagementClientController {

    // 설정파일 불러오기
    Config config = ConfigLoader.getConfig();
    String host = config.server.host;
    int port = config.server.port;

    // Singleton 인스턴스
    @Getter
    private static final RoomReservationManagementClientController instance = new RoomReservationManagementClientController();

    private RoomReservationManagementClientController() {}

    // 예약 수정
    public BasicResponse modifyRoomReservation(RoomReservationRequest roomReservation) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 수정", roomReservation);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                // System.out.println("서버 응답: " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    // 관리자 예약 삭제
    public BasicResponse deleteRoomReservation(String roomReservationId) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 삭제", roomReservationId);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                // System.out.println("서버 응답: " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    // 예약 상태가 "대기" 인 모든 예약 내역 반환
    public BasicResponse findAllRoomReservation() {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 대기 전체 조회","");
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                // System.out.println("서버 응답: " + r.data);
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    // 예약 상태 변경 "대기 -> 승인" (기존 메서드 - 하위 호환성 유지)
    public BasicResponse changeRoomReservationStatus(String roomReservationId) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 상태 변경", roomReservationId);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    /**
     * 예약 승인 (SFR-403, SFR-404)
     */
    public BasicResponse approveRoomReservation(String roomReservationId) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 승인", roomReservationId);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    /**
     * 예약 거부 (SFR-403, SFR-404)
     * @param roomReservationId 예약 ID
     * @param rejectionReason 거부 사유
     */
    public BasicResponse rejectRoomReservation(String roomReservationId, String rejectionReason) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            String[] payload = {roomReservationId, rejectionReason};
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 거부", payload);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

    /**
     * 취소 원인과 함께 예약 삭제 (SFR-405, SFR-406)
     */
    public BasicResponse deleteRoomReservation(String roomReservationId, String cancellationReason) {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            String[] payload = {roomReservationId, cancellationReason};
            ReservationManagementCommandRequest req = new ReservationManagementCommandRequest("예약 삭제", payload);
            out.writeObject(req);

            Object res = in.readObject();
            if (res instanceof BasicResponse r) {
                return r;
            }
        } catch (Exception e) {
            System.out.println("서버 통신 실패: " + e.getMessage());
        }
        return null;
    }

}
