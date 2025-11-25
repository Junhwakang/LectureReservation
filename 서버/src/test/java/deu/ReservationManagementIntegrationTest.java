package deu;

import deu.command.*;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.entity.RoomReservation;
import deu.observer.LoggingObserver;
import deu.observer.NotificationObserver;
import deu.observer.ReservationSubject;
import deu.repository.ReservationRepository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SFR-401 ~ SFR-412 통합 테스트
 * 
 * 테스트 범위:
 * - Command 패턴 (Modify, Delete, Approve, Reject)
 * - Observer 패턴 (Notification, Logging)
 * - Undo/Redo 기능
 * - 통합 시나리오
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationManagementIntegrationTest {

    private ReservationRepository repository;
    private ReservationSubject subject;
    private ReservationCommandInvoker invoker;
    
    private NotificationObserver notificationObserver;
    private LoggingObserver loggingObserver;
    
    private RoomReservation testReservation;
    private String testReservationId;
    
    // 파일 경로
    private static final String NOTIFICATION_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notifications";
    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "data" + File.separator + "logs";
    private static final String LOG_FILE = LOG_DIR + File.separator + "reservation_history.log";

    @BeforeEach
    void setUp() {
        // 레포지토리 및 Subject 초기화
        repository = ReservationRepository.getInstance();
        subject = ReservationSubject.getInstance();
        invoker = ReservationCommandInvoker.getInstance();
        
        // 기존 데이터 정리
        repository.clear();
        invoker.clearHistory();
        subject.getObservers().clear();
        
        // 옵저버 등록
        notificationObserver = new NotificationObserver();
        loggingObserver = new LoggingObserver();
        subject.attach(notificationObserver);
        subject.attach(loggingObserver);
        
        // 테스트용 예약 생성
        testReservation = createTestReservation("20201234", "대기");
        repository.save(testReservation);
        testReservationId = testReservation.getId();
        
        System.out.println("\n========== 테스트 시작 ==========");
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 정리
        repository.clear();
        invoker.clearHistory();
        cleanupTestFiles();
        
        System.out.println("========== 테스트 종료 ==========\n");
    }

    // ========== 1. Command 패턴 테스트 ==========
    
    /**
     * SFR-401, SFR-402: 예약 수정 커맨드 테스트
     */
    @Test
    @Order(1)
    @DisplayName("1. 예약 수정 커맨드 - 성공")
    void testModifyReservationCommand_Success() {
        System.out.println("▶ 테스트 1: 예약 수정");
        
        // Given: 수정할 데이터 준비
        RoomReservationRequest request = new RoomReservationRequest();
        request.setId(testReservationId);
        request.setBuildingName("B동");
        request.setFloor("5");
        request.setLectureRoom("502");
        request.setTitle("수정된 예약");
        request.setDescription("수정된 설명");
        request.setDate("2025-11-26");
        request.setDayOfTheWeek("화요일");
        request.setStartTime("14:00");
        request.setEndTime("16:00");
        
        // When: 수정 커맨드 실행
        ModifyReservationCommand command = new ModifyReservationCommand(request, subject);
        BasicResponse response = invoker.executeCommand(command);
        
        // Then: 검증
        assertEquals("200", response.code, "수정 성공 응답이어야 함");
        
        RoomReservation modified = repository.findById(testReservationId);
        assertNotNull(modified, "수정된 예약이 존재해야 함");
        assertEquals("B동", modified.getBuildingName(), "건물명이 변경되어야 함");
        assertEquals("502", modified.getLectureRoom(), "강의실이 변경되어야 함");
        assertEquals("수정된 예약", modified.getTitle(), "제목이 변경되어야 함");
        
        System.out.println("✅ 예약 수정 성공: " + modified.getTitle());
    }

    /**
     * SFR-401, SFR-402: 예약 수정 후 Undo 테스트
     */
    @Test
    @Order(2)
    @DisplayName("2. 예약 수정 후 Undo - 원본 복원")
    void testModifyReservationCommand_Undo() {
        System.out.println("▶ 테스트 2: 예약 수정 후 Undo");
        
        // Given: 원본 데이터 저장
        String originalBuilding = testReservation.getBuildingName();
        String originalTitle = testReservation.getTitle();
        
        // When: 수정 후 Undo
        RoomReservationRequest request = createModifyRequest(testReservationId, "수정된 건물", "수정된 제목");
        ModifyReservationCommand command = new ModifyReservationCommand(request, subject);
        invoker.executeCommand(command);
        
        BasicResponse undoResponse = invoker.undo();
        
        // Then: 원본으로 복원되었는지 검증
        assertEquals("200", undoResponse.code, "Undo 성공 응답이어야 함");
        
        RoomReservation restored = repository.findById(testReservationId);
        assertEquals(originalBuilding, restored.getBuildingName(), "원본 건물명으로 복원되어야 함");
        assertEquals(originalTitle, restored.getTitle(), "원본 제목으로 복원되어야 함");
        
        System.out.println("✅ Undo 성공: 원본 데이터로 복원됨");
    }

    /**
     * SFR-403, SFR-404: 예약 승인 커맨드 테스트
     */
    @Test
    @Order(3)
    @DisplayName("3. 예약 승인 커맨드 - 성공")
    void testApproveReservationCommand_Success() {
        System.out.println("▶ 테스트 3: 예약 승인");
        
        // When: 승인 커맨드 실행
        ApproveReservationCommand command = new ApproveReservationCommand(testReservationId, subject);
        BasicResponse response = invoker.executeCommand(command);
        
        // Then: 검증
        assertEquals("200", response.code, "승인 성공 응답이어야 함");
        
        RoomReservation approved = repository.findById(testReservationId);
        assertNotNull(approved, "승인된 예약이 존재해야 함");
        assertEquals("승인", approved.getStatus(), "상태가 '승인'으로 변경되어야 함");
        
        // 알림 파일 생성 확인
        assertTrue(isNotificationFileExists("20201234"), "사용자 알림 파일이 생성되어야 함");
        
        System.out.println("✅ 예약 승인 성공: 상태=" + approved.getStatus());
    }

    /**
     * SFR-403, SFR-404: 예약 거부 커맨드 테스트 (거부 사유 포함)
     */
    @Test
    @Order(4)
    @DisplayName("4. 예약 거부 커맨드 - 거부 사유 포함")
    void testRejectReservationCommand_WithReason() {
        System.out.println("▶ 테스트 4: 예약 거부 (거부 사유)");
        
        // Given: 거부 사유
        String rejectionReason = "강의실 정기 점검으로 인한 거부";
        
        // When: 거부 커맨드 실행
        RejectReservationCommand command = new RejectReservationCommand(testReservationId, rejectionReason, subject);
        BasicResponse response = invoker.executeCommand(command);
        
        // Then: 검증
        assertEquals("200", response.code, "거부 성공 응답이어야 함");
        
        RoomReservation rejected = repository.findById(testReservationId);
        assertNotNull(rejected, "거부된 예약이 존재해야 함");
        assertEquals("거부", rejected.getStatus(), "상태가 '거부'로 변경되어야 함");
        assertEquals(rejectionReason, rejected.getRejectionReason(), "거부 사유가 저장되어야 함");
        
        // 알림 파일 생성 및 내용 확인
        assertTrue(isNotificationFileExists("20201234"), "사용자 알림 파일이 생성되어야 함");
        assertTrue(isNotificationContains("20201234", rejectionReason), "알림에 거부 사유가 포함되어야 함");
        
        System.out.println("✅ 예약 거부 성공: 사유=" + rejectionReason);
    }

    /**
     * SFR-405, SFR-406: 예약 삭제 커맨드 테스트 (취소 사유 포함)
     */
    @Test
    @Order(5)
    @DisplayName("5. 예약 삭제 커맨드 - 취소 사유 포함")
    void testDeleteReservationCommand_WithCancellationReason() {
        System.out.println("▶ 테스트 5: 예약 삭제 (취소 사유)");
        
        // Given: 취소 사유
        String cancellationReason = "시설 공사로 인한 관리자 취소";
        
        // When: 삭제 커맨드 실행
        DeleteReservationCommand command = new DeleteReservationCommand(testReservationId, cancellationReason, subject);
        BasicResponse response = invoker.executeCommand(command);
        
        // Then: 검증
        assertEquals("200", response.code, "삭제 성공 응답이어야 함");
        
        RoomReservation deleted = repository.findById(testReservationId);
        assertNull(deleted, "삭제된 예약은 조회되지 않아야 함");
        
        // 알림 파일 생성 및 내용 확인 (SFR-410)
        assertTrue(isNotificationFileExists("20201234"), "사용자 알림 파일이 생성되어야 함");
        assertTrue(isNotificationContains("20201234", cancellationReason), "알림에 취소 사유가 포함되어야 함");
        
        System.out.println("✅ 예약 삭제 성공: 취소 사유=" + cancellationReason);
    }

    /**
     * SFR-405, SFR-406: 예약 삭제 후 Undo 테스트
     */
    @Test
    @Order(6)
    @DisplayName("6. 예약 삭제 후 Undo - 예약 복원")
    void testDeleteReservationCommand_Undo() {
        System.out.println("▶ 테스트 6: 예약 삭제 후 Undo");
        
        // Given: 삭제 전 데이터 저장
        String originalTitle = testReservation.getTitle();
        
        // When: 삭제 후 Undo
        DeleteReservationCommand command = new DeleteReservationCommand(testReservationId, "테스트 취소", subject);
        invoker.executeCommand(command);
        
        assertNull(repository.findById(testReservationId), "삭제 후 예약이 존재하지 않아야 함");
        
        BasicResponse undoResponse = invoker.undo();
        
        // Then: 복원 검증
        assertEquals("200", undoResponse.code, "Undo 성공 응답이어야 함");
        
        RoomReservation restored = repository.findById(testReservationId);
        assertNotNull(restored, "Undo 후 예약이 복원되어야 함");
        assertEquals(originalTitle, restored.getTitle(), "원본 제목으로 복원되어야 함");
        
        System.out.println("✅ Undo 성공: 삭제된 예약 복원됨");
    }

    // ========== 2. Observer 패턴 테스트 ==========

    /**
     * SFR-409: 예약 승인 시 사용자 알림 테스트
     */
    @Test
    @Order(7)
    @DisplayName("7. Observer - 예약 승인 시 사용자 알림")
    void testNotificationObserver_OnApproval() {
        System.out.println("▶ 테스트 7: 예약 승인 알림");
        
        // When: 예약 승인
        ApproveReservationCommand command = new ApproveReservationCommand(testReservationId, subject);
        invoker.executeCommand(command);
        
        // Then: 알림 파일 생성 확인
        String userNotificationFile = NOTIFICATION_DIR + File.separator + "user_20201234_notifications.txt";
        File notificationFile = new File(userNotificationFile);
        
        assertTrue(notificationFile.exists(), "알림 파일이 생성되어야 함");
        assertTrue(notificationFile.length() > 0, "알림 내용이 있어야 함");
        
        // 알림 내용 확인
        assertTrue(isNotificationContains("20201234", "[예약 승인]"), "승인 알림이 포함되어야 함");
        assertTrue(isNotificationContains("20201234", testReservation.getTitle()), "예약 제목이 포함되어야 함");
        
        System.out.println("✅ 사용자 알림 파일 생성됨: " + userNotificationFile);
    }

    /**
     * SFR-409: 예약 거부 시 사용자 알림 테스트
     */
    @Test
    @Order(8)
    @DisplayName("8. Observer - 예약 거부 시 사용자 알림 및 사유")
    void testNotificationObserver_OnRejection() {
        System.out.println("▶ 테스트 8: 예약 거부 알림");
        
        // Given
        String rejectionReason = "수용 인원 초과";
        
        // When: 예약 거부
        RejectReservationCommand command = new RejectReservationCommand(testReservationId, rejectionReason, subject);
        invoker.executeCommand(command);
        
        // Then: 알림 내용 확인
        assertTrue(isNotificationFileExists("20201234"), "알림 파일이 생성되어야 함");
        assertTrue(isNotificationContains("20201234", "[예약 거부]"), "거부 알림이 포함되어야 함");
        assertTrue(isNotificationContains("20201234", rejectionReason), "거부 사유가 포함되어야 함");
        
        System.out.println("✅ 거부 알림 생성됨: 사유=" + rejectionReason);
    }

    /**
     * SFR-410: 관리자에 의한 예약 취소 시 사용자 알림 테스트
     */
    @Test
    @Order(9)
    @DisplayName("9. Observer - 예약 취소 시 사용자 알림 및 사유")
    void testNotificationObserver_OnCancellation() {
        System.out.println("▶ 테스트 9: 예약 취소 알림");
        
        // Given
        String cancellationReason = "긴급 시설 보수";
        
        // When: 예약 취소
        DeleteReservationCommand command = new DeleteReservationCommand(testReservationId, cancellationReason, subject);
        invoker.executeCommand(command);
        
        // Then: 알림 내용 확인
        assertTrue(isNotificationFileExists("20201234"), "알림 파일이 생성되어야 함");
        assertTrue(isNotificationContains("20201234", "[예약 취소]"), "취소 알림이 포함되어야 함");
        assertTrue(isNotificationContains("20201234", cancellationReason), "취소 사유가 포함되어야 함");
        
        System.out.println("✅ 취소 알림 생성됨: 사유=" + cancellationReason);
    }

    /**
     * SFR-412: 예약 변경 이력 로깅 테스트
     */
    @Test
    @Order(10)
    @DisplayName("10. Observer - 예약 변경 이력 로깅")
    void testLoggingObserver_OnReservationModified() {
        System.out.println("▶ 테스트 10: 예약 변경 이력 로깅");
        
        // Given: 수정 요청
        RoomReservationRequest request = createModifyRequest(testReservationId, "C동", "로깅 테스트");
        
        // When: 수정 커맨드 실행
        ModifyReservationCommand command = new ModifyReservationCommand(request, subject);
        invoker.executeCommand(command);
        
        // Then: 로그 파일 생성 및 내용 확인
        File logFile = new File(LOG_FILE);
        assertTrue(logFile.exists(), "로그 파일이 생성되어야 함");
        assertTrue(logFile.length() > 0, "로그 내용이 있어야 함");
        
        assertTrue(isLogContains("[수정]"), "수정 로그가 포함되어야 함");
        assertTrue(isLogContains(testReservationId), "예약 ID가 포함되어야 함");
        
        System.out.println("✅ 변경 이력 로그 생성됨: " + LOG_FILE);
    }

    /**
     * 새 예약 신청 시 관리자 알림 테스트
     */
    @Test
    @Order(11)
    @DisplayName("11. Observer - 새 예약 신청 시 관리자 알림")
    void testNotificationObserver_OnReservationCreated() {
        System.out.println("▶ 테스트 11: 새 예약 신청 시 관리자 알림");
        
        // Given: 새 예약 생성
        RoomReservation newReservation = createTestReservation("20205678", "대기");
        
        // When: 새 예약 알림
        subject.notifyReservationCreated(newReservation);
        
        // Then: 관리자 알림 파일 확인
        assertTrue(isNotificationFileExists("admin"), "관리자 알림 파일이 생성되어야 함");
        assertTrue(isNotificationContains("admin", "[새 예약 신청]"), "새 예약 알림이 포함되어야 함");
        assertTrue(isNotificationContains("admin", "20205678"), "신청자 학번이 포함되어야 함");
        
        System.out.println("✅ 관리자 알림 생성됨: 신청자=20205678");
    }

    // ========== 3. Undo/Redo 테스트 ==========

    /**
     * Undo/Redo 통합 테스트
     */
    @Test
    @Order(12)
    @DisplayName("12. Undo/Redo - 여러 커맨드 실행 후 Undo/Redo")
    void testCommandInvoker_UndoRedo() {
        System.out.println("▶ 테스트 12: Undo/Redo 통합");
        
        // Given: 여러 커맨드 실행
        // 1. 승인
        ApproveReservationCommand approveCmd = new ApproveReservationCommand(testReservationId, subject);
        invoker.executeCommand(approveCmd);
        assertEquals("승인", repository.findById(testReservationId).getStatus());
        
        // 2. 수정
        RoomReservationRequest modifyReq = createModifyRequest(testReservationId, "D동", "수정됨");
        ModifyReservationCommand modifyCmd = new ModifyReservationCommand(modifyReq, subject);
        invoker.executeCommand(modifyCmd);
        assertEquals("D동", repository.findById(testReservationId).getBuildingName());
        
        // When: Undo 두 번
        invoker.undo(); // 수정 취소
        assertEquals("A동", repository.findById(testReservationId).getBuildingName(), "수정 취소됨");
        
        invoker.undo(); // 승인 취소
        assertEquals("대기", repository.findById(testReservationId).getStatus(), "승인 취소됨");
        
        // Then: Redo 두 번
        invoker.redo(); // 승인 재실행
        assertEquals("승인", repository.findById(testReservationId).getStatus(), "승인 재실행됨");
        
        invoker.redo(); // 수정 재실행
        assertEquals("D동", repository.findById(testReservationId).getBuildingName(), "수정 재실행됨");
        
        System.out.println("✅ Undo/Redo 성공");
    }

    /**
     * 커맨드 히스토리 조회 테스트
     */
    @Test
    @Order(13)
    @DisplayName("13. 커맨드 히스토리 - 실행된 커맨드 목록 조회")
    void testCommandInvoker_GetHistory() {
        System.out.println("▶ 테스트 13: 커맨드 히스토리");
        
        // Given: 여러 커맨드 실행
        ApproveReservationCommand cmd1 = new ApproveReservationCommand(testReservationId, subject);
        invoker.executeCommand(cmd1);
        
        RoomReservationRequest req = createModifyRequest(testReservationId, "E동", "히스토리 테스트");
        ModifyReservationCommand cmd2 = new ModifyReservationCommand(req, subject);
        invoker.executeCommand(cmd2);
        
        // When: 히스토리 조회
        String history = invoker.getCommandHistory();
        
        // Then: 검증
        assertNotNull(history, "히스토리가 null이 아니어야 함");
        assertTrue(history.contains("예약 승인"), "승인 커맨드가 포함되어야 함");
        assertTrue(history.contains("예약 수정"), "수정 커맨드가 포함되어야 함");
        
        System.out.println("✅ 커맨드 히스토리:\n" + history);
    }

    // ========== 4. 통합 시나리오 테스트 ==========

    /**
     * 전체 시나리오 테스트: 예약 신청 → 승인 → 알림 → 로그
     */
    @Test
    @Order(14)
    @DisplayName("14. 통합 시나리오 - 예약 신청부터 승인까지")
    void testIntegrationScenario_ReservationApprovalFlow() {
        System.out.println("▶ 테스트 14: 통합 시나리오");
        
        // Scenario 1: 새 예약 신청
        RoomReservation newReservation = createTestReservation("20209999", "대기");
        repository.save(newReservation);
        subject.notifyReservationCreated(newReservation);
        
        // 관리자 알림 확인
        assertTrue(isNotificationFileExists("admin"), "관리자 알림 생성됨");
        
        // Scenario 2: 관리자가 예약 승인
        ApproveReservationCommand approveCmd = new ApproveReservationCommand(newReservation.getId(), subject);
        BasicResponse approveResponse = invoker.executeCommand(approveCmd);
        
        assertEquals("200", approveResponse.code, "승인 성공");
        assertEquals("승인", repository.findById(newReservation.getId()).getStatus(), "상태 변경됨");
        
        // Scenario 3: 사용자 알림 및 로그 확인
        assertTrue(isNotificationFileExists("20209999"), "사용자 알림 생성됨");
        assertTrue(isLogContains("[승인]"), "로그 기록됨");
        
        System.out.println("✅ 통합 시나리오 완료");
    }

    /**
     * 전체 시나리오 테스트: 예약 거부 흐름
     */
    @Test
    @Order(15)
    @DisplayName("15. 통합 시나리오 - 예약 거부 흐름")
    void testIntegrationScenario_ReservationRejectionFlow() {
        System.out.println("▶ 테스트 15: 예약 거부 흐름");
        
        // Given: 거부 사유
        String reason = "강의실 이용 규정 위반";
        
        // When: 예약 거부
        RejectReservationCommand rejectCmd = new RejectReservationCommand(testReservationId, reason, subject);
        BasicResponse response = invoker.executeCommand(rejectCmd);
        
        // Then: 전체 검증
        assertEquals("200", response.code, "거부 성공");
        assertEquals("거부", repository.findById(testReservationId).getStatus(), "상태 변경됨");
        assertEquals(reason, repository.findById(testReservationId).getRejectionReason(), "거부 사유 저장됨");
        
        assertTrue(isNotificationFileExists("20201234"), "사용자 알림 생성됨");
        assertTrue(isNotificationContains("20201234", reason), "알림에 사유 포함됨");
        assertTrue(isLogContains("[거부]"), "로그 기록됨");
        
        System.out.println("✅ 예약 거부 흐름 완료");
    }

    /**
     * 다중 옵저버 알림 테스트
     */
    @Test
    @Order(16)
    @DisplayName("16. 다중 옵저버 - 알림과 로그가 동시에 작동")
    void testMultipleObservers_Simultaneously() {
        System.out.println("▶ 테스트 16: 다중 옵저버");
        
        // When: 예약 승인 (NotificationObserver + LoggingObserver 동시 작동)
        ApproveReservationCommand command = new ApproveReservationCommand(testReservationId, subject);
        invoker.executeCommand(command);
        
        // Then: 두 옵저버 모두 작동했는지 확인
        assertTrue(isNotificationFileExists("20201234"), "NotificationObserver 작동");
        assertTrue(new File(LOG_FILE).exists(), "LoggingObserver 작동");
        assertTrue(isLogContains("[승인]"), "로그에 승인 기록됨");
        
        System.out.println("✅ 다중 옵저버 동시 작동 확인");
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * 테스트용 예약 생성
     */
    private RoomReservation createTestReservation(String studentNumber, String status) {
        RoomReservation reservation = new RoomReservation();
        reservation.setBuildingName("A동");
        reservation.setFloor("3");
        reservation.setLectureRoom("301");
        reservation.setNumber(studentNumber);
        reservation.setStatus(status);
        reservation.setTitle("테스트 예약");
        reservation.setDescription("통합 테스트용 예약");
        reservation.setDate("2025-11-25");
        reservation.setDayOfTheWeek("월요일");
        reservation.setStartTime("09:00");
        reservation.setEndTime("11:00");
        reservation.setPurpose("개인학습");
        reservation.setParticipantCount(5);
        reservation.setCapacity(30);
        return reservation;
    }

    /**
     * 수정 요청 생성
     */
    private RoomReservationRequest createModifyRequest(String id, String buildingName, String title) {
        RoomReservationRequest request = new RoomReservationRequest();
        request.setId(id);
        request.setBuildingName(buildingName);
        request.setFloor("3");
        request.setLectureRoom("301");
        request.setTitle(title);
        request.setDescription("수정된 설명");
        request.setDate("2025-11-25");
        request.setDayOfTheWeek("월요일");
        request.setStartTime("09:00");
        request.setEndTime("11:00");
        return request;
    }

    /**
     * 알림 파일 존재 확인
     */
    private boolean isNotificationFileExists(String userNumber) {
        String filePath = NOTIFICATION_DIR + File.separator + "user_" + userNumber + "_notifications.txt";
        return new File(filePath).exists();
    }

    /**
     * 알림 파일에 특정 문자열 포함 확인
     */
    private boolean isNotificationContains(String userNumber, String content) {
        try {
            String filePath = NOTIFICATION_DIR + File.separator + "user_" + userNumber + "_notifications.txt";
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return false;
            
            List<String> lines = Files.readAllLines(path);
            return lines.stream().anyMatch(line -> line.contains(content));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 로그 파일에 특정 문자열 포함 확인
     */
    private boolean isLogContains(String content) {
        try {
            Path path = Paths.get(LOG_FILE);
            if (!Files.exists(path)) return false;
            
            List<String> lines = Files.readAllLines(path);
            return lines.stream().anyMatch(line -> line.contains(content));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 테스트 파일 정리
     */
    private void cleanupTestFiles() {
        // 알림 파일 삭제
        File notificationDir = new File(NOTIFICATION_DIR);
        if (notificationDir.exists()) {
            File[] files = notificationDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
        
        // 로그 파일 삭제
        File logFile = new File(LOG_FILE);
        if (logFile.exists()) {
            logFile.delete();
        }
    }
}
