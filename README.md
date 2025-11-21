# 강의실 예약 시스템 - 디자인 패턴 리팩토링

## 🎯 적용된 디자인 패턴
- **커맨드 패턴 (Command Pattern)**: 예약 관리 작업 캡슐화
- **옵저버 패턴 (Observer Pattern)**: 자동 알림 시스템

---

## 🚀 실행 방법

### 1. 서버 실행
```bash
cd 서버
java -jar DeuLectureRoomServer-1.0.0.jar
```

### 2. 클라이언트 실행
```bash
cd 클라이언트
java -jar DeuLectureRoomApp-1.0.0.jar
```

---

## 📋 테스트 시나리오

### 테스트 1: 새 예약 신청 (관리자 알림)
1. **사용자 계정**으로 로그인
2. 예약 화면에서 강의실 선택
3. 날짜/시간 선택 후 예약 신청
4. ✅ **확인**: `/서버/data/notifications/user_admin_notifications.txt` 파일 생성 확인

### 테스트 2: 예약 승인 (사용자 알림)
1. **관리자 계정**으로 로그인
2. 예약 관리 화면 열기
3. 대기 중인 예약 선택 후 **승인** 클릭
4. ✅ **확인**: `/서버/data/notifications/user_{학번}_notifications.txt` 파일에 승인 알림 기록

### 테스트 3: 예약 거부 + 사유 (사용자 알림)
1. **관리자 계정**으로 로그인
2. 예약 관리 화면에서 대기 중인 예약 선택
3. **거부** 클릭 → 거부 사유 입력창에 "강의실 정기 점검" 입력
4. ✅ **확인**: 
   - 알림 파일에 거부 사유 포함 확인
   - 예약 상태가 "거부"로 변경

### 테스트 4: 예약 삭제 + 취소 사유 (사용자 알림)
1. **관리자 계정**으로 로그인
2. 예약 관리 화면에서 예약 선택
3. **삭제** 클릭 → 취소 사유 입력창에 "시설 공사" 입력
4. ✅ **확인**:
   - 알림 파일에 취소 사유 포함 확인
   - 예약이 완전히 삭제됨

### 테스트 5: Undo/Redo 기능
1. **관리자 계정**으로 로그인
2. 예약 승인 실행
3. **Undo** 버튼 클릭 → 예약이 "대기" 상태로 복원되는지 확인
4. **Redo** 버튼 클릭 → 다시 "승인" 상태로 변경되는지 확인

### 테스트 6: 변경 이력 로그
1. 위 테스트들 실행 후
2. `/서버/data/logs/reservation_history.log` 파일 열기
3. ✅ **확인**: 모든 작업이 시간순으로 기록되어 있는지 확인

---

## 📂 중요 파일 위치

### 알림 파일
```
서버/data/notifications/
├── user_admin_notifications.txt      # 관리자용 (새 예약 신청 알림)
└── user_{학번}_notifications.txt     # 사용자용 (승인/거부/취소 알림)
```

### 로그 파일
```
서버/data/logs/
└── reservation_history.log           # 모든 예약 변경 이력
```

### 예약 데이터
```
서버/data/
└── reservations.yaml                 # 예약 정보 (취소/거부 사유 포함)
```

---

## 🎨 새로 추가된 기능

### 1. 커맨드 패턴 기능
- ✅ **Undo (실행 취소)**: 마지막 작업 취소
- ✅ **Redo (재실행)**: 취소한 작업 다시 실행
- ✅ **Command History**: 모든 작업 이력 조회

### 2. 옵저버 패턴 기능
- ✅ **관리자 알림**: 새 예약 신청 시 자동 알림
- ✅ **사용자 알림**: 예약 승인/거부/취소 시 자동 알림
- ✅ **변경 이력 로깅**: 모든 예약 변경 사항 자동 기록
- ✅ **파일 기반 저장**: 알림과 로그를 파일로 영구 저장

### 3. 새로운 필드
- ✅ **cancellationReason**: 예약 취소 사유
- ✅ **rejectionReason**: 예약 거부 사유

---

## 🔍 코드 구조

### 커맨드 패턴 클래스
```
서버/src/main/java/deu/command/
├── ReservationCommand.java          # 인터페이스
├── ReservationCommandInvoker.java   # 실행자 (Undo/Redo 관리)
├── ModifyReservationCommand.java    # 수정 커맨드
├── DeleteReservationCommand.java    # 삭제 커맨드
├── ApproveReservationCommand.java   # 승인 커맨드
└── RejectReservationCommand.java    # 거부 커맨드
```

### 옵저버 패턴 클래스
```
서버/src/main/java/deu/observer/
├── ReservationObserver.java      # 인터페이스
├── ReservationSubject.java       # Subject (이벤트 발생자)
├── NotificationObserver.java     # 알림 옵저버
└── LoggingObserver.java          # 로깅 옵저버
```

---

## ⚠️ 주의사항

1. **서버를 먼저 실행**해야 클라이언트가 정상 작동합니다
2. **관리자 계정** 정보:
   - ID: admin
   - PW: (기존 설정된 비밀번호)
3. **알림 파일**은 서버 실행 디렉토리의 `data/notifications/` 폴더에 생성됩니다
4. **로그 파일**은 서버 실행 디렉토리의 `data/logs/` 폴더에 생성됩니다

---

## 📄 상세 문서

자세한 내용은 `리팩토링_완료_보고서.md` 파일을 참고하세요.

---

## 👨‍💻 개발자
- **담당자**: 강준화
- **적용 패턴**: 커맨드 패턴 + 옵저버 패턴
- **완료 일자**: 2025-11-21

---

✨ **본 시스템은 SOLID 원칙을 준수하며, 유지보수와 확장이 용이한 구조로 설계되었습니다.**
