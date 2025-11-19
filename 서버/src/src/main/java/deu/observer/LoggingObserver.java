package deu.observer;

import deu.model.entity.RoomReservation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 옵저버 패턴 - Concrete Observer
 * 예약 변경 내역을 로그 파일에 기록하는 옵저버
 */
public class LoggingObserver implements ReservationObserver {
    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "logs";
    private static final String LOG_FILE = LOG_DIR + File.separator + "reservation_changes.log";
    
    public LoggingObserver() {
        // 로그 디렉토리 생성
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void update(RoomReservation reservation, String message) {
        try {
            String logEntry = createLogEntry(reservation, message);
            saveLog(logEntry);
            System.out.println("📝 로그 기록: " + message);
        } catch (Exception e) {
            System.err.println("⚠ 로그 기록 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 로그 엔트리 생성
     */
    private String createLogEntry(RoomReservation reservation, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        return String.format("[%s] 예약ID: %s | 사용자: %s | 강의실: %s %s층 %s | 일시: %s %s~%s | 상태: %s | %s%n",
                timestamp,
                reservation.getId(),
                reservation.getNumber(),
                reservation.getBuildingName(),
                reservation.getFloor(),
                reservation.getLectureRoom(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                message
        );
    }

    /**
     * 로그 파일에 저장
     */
    private void saveLog(String logEntry) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("⚠ 로그 파일 저장 실패: " + e.getMessage());
        }
    }
}
