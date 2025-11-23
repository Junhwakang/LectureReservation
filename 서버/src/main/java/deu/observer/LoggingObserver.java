package deu.observer;

import deu.model.entity.RoomReservation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 로깅 옵저버 (SFR-412)
 * 예약 변경 이력을 로그 파일로 저장
 */
public class LoggingObserver implements ReservationObserver {
    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "logs";
    private static final String LOG_FILE = LOG_DIR + File.separator + "reservation_history.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public LoggingObserver() {
        // 로그 디렉토리 생성
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public void onReservationApproved(RoomReservation reservation) {
        String logMessage = String.format("[%s] [승인] 예약 ID: %s | 사용자: %s | 강의실: %s %s층 %s호 | 날짜: %s %s~%s",
            LocalDateTime.now().format(formatter),
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getStartTime(),
            reservation.getEndTime()
        );
        
        writeLog(logMessage);
    }
    
    @Override
    public void onReservationRejected(RoomReservation reservation, String reason) {
        String logMessage = String.format("[%s] [거부] 예약 ID: %s | 사용자: %s | 강의실: %s %s층 %s호 | 날짜: %s %s~%s | 사유: %s",
            LocalDateTime.now().format(formatter),
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reason
        );
        
        writeLog(logMessage);
    }
    
    @Override
    public void onReservationCancelled(RoomReservation reservation, String reason) {
        String logMessage = String.format("[%s] [취소] 예약 ID: %s | 사용자: %s | 강의실: %s %s층 %s호 | 날짜: %s %s~%s | 취소 사유: %s",
            LocalDateTime.now().format(formatter),
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reason
        );
        
        writeLog(logMessage);
    }
    
    @Override
    public void onReservationModified(RoomReservation reservation) {
        String logMessage = String.format("[%s] [수정] 예약 ID: %s | 사용자: %s | 강의실: %s %s층 %s호 | 날짜: %s %s~%s",
            LocalDateTime.now().format(formatter),
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getStartTime(),
            reservation.getEndTime()
        );
        
        writeLog(logMessage);
    }
    
    @Override
    public void onReservationCreated(RoomReservation reservation) {
        String logMessage = String.format("[%s] [새 예약 신청] 예약 ID: %s | 사용자: %s | 강의실: %s %s층 %s호 | 날짜: %s %s~%s",
            LocalDateTime.now().format(formatter),
            reservation.getId(),
            reservation.getNumber(),
            reservation.getBuildingName(),
            reservation.getFloor(),
            reservation.getLectureRoom(),
            reservation.getDate(),
            reservation.getStartTime(),
            reservation.getEndTime()
        );
        
        writeLog(logMessage);
    }
    
    /**
     * 로그를 파일에 기록
     */
    private void writeLog(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + "\n");
            System.out.println("📋 " + message);
        } catch (IOException e) {
            System.err.println("로그 기록 실패: " + e.getMessage());
        }
    }
}
