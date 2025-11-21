package deu.observer;

import deu.model.entity.RoomReservation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 사용자 알림 옵저버 (SFR-409, SFR-410)
 * 예약 상태 변경 시 사용자에게 알림을 파일로 저장
 */
public class NotificationObserver implements ReservationObserver {
    private static final String NOTIFICATION_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notifications";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public NotificationObserver() {
        // 알림 디렉토리 생성
        File dir = new File(NOTIFICATION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public void onReservationApproved(RoomReservation reservation) {
        String message = String.format(
            "[예약 승인] %s\n" +
            "건물: %s %s층 %s호\n" +
            "날짜: %s (%s) %s~%s\n" +
            "제목: %s\n" +
            "상태: 승인됨\n" +
            "시간: %s\n",
            reservation.getId(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getDayOfTheWeek(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTitle(),
            LocalDateTime.now().format(formatter)
        );
        
        saveNotification(reservation.getNumber(), message);
        System.out.println("✅ [알림] 사용자 " + reservation.getNumber() + "에게 예약 승인 알림 전송");
    }
    
    @Override
    public void onReservationRejected(RoomReservation reservation, String reason) {
        String message = String.format(
            "[예약 거부] %s\n" +
            "건물: %s %s층 %s호\n" +
            "날짜: %s (%s) %s~%s\n" +
            "제목: %s\n" +
            "상태: 거부됨\n" +
            "거부 사유: %s\n" +
            "시간: %s\n",
            reservation.getId(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getDayOfTheWeek(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTitle(),
            reason,
            LocalDateTime.now().format(formatter)
        );
        
        saveNotification(reservation.getNumber(), message);
        System.out.println("⚠️  [알림] 사용자 " + reservation.getNumber() + "에게 예약 거부 알림 전송");
    }
    
    @Override
    public void onReservationCancelled(RoomReservation reservation, String reason) {
        String message = String.format(
            "[예약 취소] %s\n" +
            "건물: %s %s층 %s호\n" +
            "날짜: %s (%s) %s~%s\n" +
            "제목: %s\n" +
            "상태: 관리자에 의해 취소됨\n" +
            "취소 사유: %s\n" +
            "시간: %s\n",
            reservation.getId(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getDayOfTheWeek(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTitle(),
            reason,
            LocalDateTime.now().format(formatter)
        );
        
        saveNotification(reservation.getNumber(), message);
        System.out.println("🔔 [알림] 사용자 " + reservation.getNumber() + "에게 예약 취소 알림 전송");
    }
    
    @Override
    public void onReservationModified(RoomReservation reservation) {
        String message = String.format(
            "[예약 변경] %s\n" +
            "건물: %s %s층 %s호\n" +
            "날짜: %s (%s) %s~%s\n" +
            "제목: %s\n" +
            "상태: 예약 정보가 수정됨\n" +
            "시간: %s\n",
            reservation.getId(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getDayOfTheWeek(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTitle(),
            LocalDateTime.now().format(formatter)
        );
        
        saveNotification(reservation.getNumber(), message);
        System.out.println("📝 [알림] 사용자 " + reservation.getNumber() + "에게 예약 변경 알림 전송");
    }
    
    @Override
    public void onReservationCreated(RoomReservation reservation) {
        // 관리자에게 새 예약 신청 알림
        String message = String.format(
            "[새 예약 신청] %s\n" +
            "신청자: %s\n" +
            "건물: %s %s층 %s호\n" +
            "날짜: %s (%s) %s~%s\n" +
            "제목: %s\n" +
            "설명: %s\n" +
            "상태: 대기\n" +
            "시간: %s\n",
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getDayOfTheWeek(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTitle(),
            reservation.getDescription(),
            LocalDateTime.now().format(formatter)
        );
        
        // 관리자 알림 파일에 저장 (admin 계정)
        saveNotification("admin", message);
        System.out.println("🔔 [알림] 관리자에게 새 예약 신청 알림 전송");
    }
    
    /**
     * 알림을 파일로 저장
     */
    private void saveNotification(String userNumber, String message) {
        String fileName = NOTIFICATION_DIR + File.separator + "user_" + userNumber + "_notifications.txt";
        
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("========================================\n");
            writer.write(message);
            writer.write("========================================\n\n");
        } catch (IOException e) {
            System.err.println("알림 저장 실패: " + e.getMessage());
        }
    }
}
