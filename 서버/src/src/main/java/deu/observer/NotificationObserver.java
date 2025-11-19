package deu.observer;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.model.entity.User;
import deu.repository.UserRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 옵저버 패턴 - Concrete Observer
 * 사용자에게 알림을 전달하는 옵저버
 * SFR-405~412: 예약 상태 변경 시 사용자에게 알림 전달
 */
public class NotificationObserver implements ReservationObserver {
    private static final String NOTIFICATION_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notifications";
    
    public NotificationObserver() {
        // 알림 디렉토리 생성
        File dir = new File(NOTIFICATION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void update(RoomReservation reservation, String message) {
        try {
            // 사용자 정보 조회 (BasicResponse로 반환됨)
            BasicResponse response = UserRepository.getInstance().findByNumber(reservation.getNumber());
            
            if (!"200".equals(response.code) || !(response.data instanceof User)) {
                System.err.println("⚠ 사용자를 찾을 수 없습니다: " + reservation.getNumber());
                return;
            }
            
            User user = (User) response.data;

            // 알림 생성
            String notification = createNotification(reservation, user, message);
            
            // 알림 파일에 저장
            saveNotification(reservation.getNumber(), notification);
            
            // 콘솔에 로그 출력
            System.out.println("📢 알림 전송 [" + user.getName() + " (" + user.getNumber() + ")]: " + message);
            
        } catch (Exception e) {
            System.err.println("⚠ 알림 전송 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 알림 메시지 생성
     */
    private String createNotification(RoomReservation reservation, User user, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("📢 예약 알림\n");
        sb.append("================================\n");
        sb.append("수신자: ").append(user.getName()).append(" (").append(user.getNumber()).append(")\n");
        sb.append("시간: ").append(timestamp).append("\n");
        sb.append("--------------------------------\n");
        sb.append("예약 정보:\n");
        sb.append("  - 제목: ").append(reservation.getTitle()).append("\n");
        sb.append("  - 강의실: ").append(reservation.getBuildingName())
          .append(" ").append(reservation.getFloor()).append("층 ")
          .append(reservation.getLectureRoom()).append("\n");
        sb.append("  - 일시: ").append(reservation.getDate())
          .append(" (").append(reservation.getDayOfTheWeek()).append(") ")
          .append(reservation.getStartTime()).append("~").append(reservation.getEndTime()).append("\n");
        sb.append("  - 상태: ").append(reservation.getStatus()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("메시지: ").append(message).append("\n");
        sb.append("================================\n\n");
        
        return sb.toString();
    }

    /**
     * 알림을 파일에 저장
     */
    private void saveNotification(String userNumber, String notification) {
        String filename = NOTIFICATION_DIR + File.separator + userNumber + "_notifications.txt";
        
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(notification);
        } catch (IOException e) {
            System.err.println("⚠ 알림 파일 저장 실패: " + e.getMessage());
        }
    }
}
