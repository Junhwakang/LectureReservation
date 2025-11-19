package deu.observer;

import deu.model.entity.RoomReservation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 옵저버 패턴 - Concrete Observer
 * 관리자에게 알림을 전달하는 옵저버
 * 새로운 예약 신청 시 관리자에게 알림
 */
public class AdminNotificationObserver implements ReservationObserver {
    private static final String NOTIFICATION_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notifications";
    private static final String ADMIN_NOTIFICATION_FILE = NOTIFICATION_DIR + File.separator + "admin_notifications.txt";
    
    public AdminNotificationObserver() {
        // 알림 디렉토리 생성
        File dir = new File(NOTIFICATION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void update(RoomReservation reservation, String message) {
        try {
            // 관리자용 알림 생성
            String notification = createAdminNotification(reservation, message);
            
            // 알림 파일에 저장
            saveNotification(notification);
            
            // 콘솔에 로그 출력
            System.out.println("🔔 [관리자 알림] 새로운 예약 신청 - " + reservation.getTitle() + 
                             " (신청자: " + reservation.getNumber() + ")");
            
        } catch (Exception e) {
            System.err.println("⚠ 관리자 알림 전송 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 관리자용 알림 메시지 생성
     */
    private String createAdminNotification(RoomReservation reservation, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("🔔 관리자 알림 - 새로운 예약 신청\n");
        sb.append("================================\n");
        sb.append("수신: 시스템 관리자\n");
        sb.append("시간: ").append(timestamp).append("\n");
        sb.append("--------------------------------\n");
        sb.append("예약 정보:\n");
        sb.append("  - 예약 ID: ").append(reservation.getId()).append("\n");
        sb.append("  - 신청자: ").append(reservation.getNumber()).append("\n");
        sb.append("  - 제목: ").append(reservation.getTitle()).append("\n");
        sb.append("  - 강의실: ").append(reservation.getBuildingName())
          .append(" ").append(reservation.getFloor()).append("층 ")
          .append(reservation.getLectureRoom()).append("\n");
        sb.append("  - 일시: ").append(reservation.getDate())
          .append(" (").append(reservation.getDayOfTheWeek()).append(") ")
          .append(reservation.getStartTime()).append("~").append(reservation.getEndTime()).append("\n");
        sb.append("  - 상태: ").append(reservation.getStatus()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("안내: ").append(message).append("\n");
        sb.append("조치 필요: 예약 관리 메뉴에서 승인/거부 처리해주세요.\n");
        sb.append("================================\n\n");
        
        return sb.toString();
    }

    /**
     * 알림을 파일에 저장
     */
    private void saveNotification(String notification) {
        try (FileWriter writer = new FileWriter(ADMIN_NOTIFICATION_FILE, true)) {
            writer.write(notification);
        } catch (IOException e) {
            System.err.println("⚠ 관리자 알림 파일 저장 실패: " + e.getMessage());
        }
    }
}
