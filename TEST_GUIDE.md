# SFR-400번대 통합 테스트 가이드

## 📋 개요

이 문서는 **강의실 예약 시스템의 SFR-401 ~ SFR-412 요구사항**에 대한 통합 테스트 가이드입니다.

### 테스트 파일 위치
```
서버/src/test/java/deu/ReservationManagementIntegrationTest.java
```

### 테스트 범위
- ✅ **Command 패턴** (예약 수정, 삭제, 승인, 거부)
- ✅ **Observer 패턴** (사용자 알림, 로깅)
- ✅ **Undo/Redo 기능**
- ✅ **통합 시나리오**

---

## 🎯 테스트 커버리지

| 테스트 번호 | 테스트 이름 | SFR 요구사항 | 상태 |
|------------|-----------|-------------|------|
| 1 | 예약 수정 커맨드 - 성공 | SFR-401, SFR-402 | ✅ |
| 2 | 예약 수정 후 Undo - 원본 복원 | SFR-401, SFR-402 | ✅ |
| 3 | 예약 승인 커맨드 - 성공 | SFR-403, SFR-404 | ✅ |
| 4 | 예약 거부 커맨드 - 거부 사유 포함 | SFR-403, SFR-404 | ✅ |
| 5 | 예약 삭제 커맨드 - 취소 사유 포함 | SFR-405, SFR-406 | ✅ |
| 6 | 예약 삭제 후 Undo - 예약 복원 | SFR-405, SFR-406 | ✅ |
| 7 | Observer - 예약 승인 시 사용자 알림 | SFR-409 | ✅ |
| 8 | Observer - 예약 거부 시 사용자 알림 및 사유 | SFR-409 | ✅ |
| 9 | Observer - 예약 취소 시 사용자 알림 및 사유 | SFR-410 | ✅ |
| 10 | Observer - 예약 변경 이력 로깅 | SFR-412 | ✅ |
| 11 | Observer - 새 예약 신청 시 관리자 알림 | 추가 기능 | ✅ |
| 12 | Undo/Redo - 여러 커맨드 실행 후 Undo/Redo | 커맨드 패턴 | ✅ |
| 13 | 커맨드 히스토리 - 실행된 커맨드 목록 조회 | 커맨드 패턴 | ✅ |
| 14 | 통합 시나리오 - 예약 신청부터 승인까지 | 통합 | ✅ |
| 15 | 통합 시나리오 - 예약 거부 흐름 | 통합 | ✅ |
| 16 | 다중 옵저버 - 알림과 로그가 동시에 작동 | 옵저버 패턴 | ✅ |

**총 16개 테스트 케이스**

---

## 🚀 테스트 실행 방법

### 1️⃣ IntelliJ IDEA에서 실행

1. **테스트 파일 열기**
   ```
   서버/src/test/java/deu/ReservationManagementIntegrationTest.java
   ```

2. **전체 테스트 실행**
   - 파일 우클릭 → `Run 'ReservationManagementIntegrationTest'`
   - 또는 단축키: `Ctrl + Shift + F10` (Windows/Linux) / `Cmd + Shift + R` (Mac)

3. **개별 테스트 실행**
   - 특정 테스트 메서드에 커서를 놓고
   - 우클릭 → `Run 'testMethodName()'`
   - 또는 메서드 왼쪽의 ▶️ 아이콘 클릭

### 2️⃣ Maven 명령어로 실행

```bash
# 서버 디렉토리로 이동
cd 서버

# 전체 테스트 실행
mvn test

# 특정 테스트 클래스만 실행
mvn test -Dtest=ReservationManagementIntegrationTest

# 특정 테스트 메서드만 실행
mvn test -Dtest=ReservationManagementIntegrationTest#testApproveReservationCommand_Success
```

### 3️⃣ Gradle로 실행 (Gradle 사용 시)

```bash
# 서버 디렉토리로 이동
cd 서버

# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스만 실행
./gradlew test --tests ReservationManagementIntegrationTest

# 특정 테스트 메서드만 실행
./gradlew test --tests ReservationManagementIntegrationTest.testApproveReservationCommand_Success
```

---

## 📊 테스트 결과 확인

### 콘솔 출력 예시

```
========== 테스트 시작 ==========
▶ 테스트 1: 예약 수정
✅ 예약 수정 성공: 수정된 예약
========== 테스트 종료 ==========

========== 테스트 시작 ==========
▶ 테스트 2: 예약 수정 후 Undo
✅ Undo 성공: 원본 데이터로 복원됨
========== 테스트 종료 ==========

...

[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 생성되는 파일들

테스트 실행 중 다음 파일들이 생성됩니다:

```
서버/data/
├── notifications/                      # 사용자 알림 파일
│   ├── user_20201234_notifications.txt
│   ├── user_admin_notifications.txt
│   └── ...
└── logs/                               # 로그 파일
    └── reservation_history.log
```

**참고**: 테스트 종료 시 자동으로 정리됩니다.

---

## 🔍 주요 테스트 시나리오

### 시나리오 1: 예약 수정 및 Undo

```java
1. 예약 정보 수정 (건물명: A동 → B동)
2. 수정 성공 확인
3. Undo 실행
4. 원본으로 복원 확인 (건물명: B동 → A동)
```

### 시나리오 2: 예약 승인 및 알림

```java
1. 예약 상태: "대기" → "승인"
2. 사용자 알림 파일 생성 확인
3. 로그 파일에 승인 기록 확인
```

### 시나리오 3: 예약 거부 (거부 사유 포함)

```java
1. 예약 거부 + 사유: "강의실 정기 점검"
2. 상태: "대기" → "거부"
3. 거부 사유 저장 확인
4. 알림에 거부 사유 포함 확인
```

### 시나리오 4: 예약 삭제 및 복원

```java
1. 예약 삭제 + 취소 사유: "시설 공사"
2. 예약 삭제 확인
3. Undo 실행
4. 예약 복원 확인
```

### 시나리오 5: 다중 커맨드 Undo/Redo

```java
1. 승인 → 수정
2. Undo 2회 (수정 취소 → 승인 취소)
3. Redo 2회 (승인 재실행 → 수정 재실행)
```

---

## ⚠️ 주의사항

### 1. 싱글톤 초기화

- `ReservationRepository.getInstance()`
- `ReservationSubject.getInstance()`
- `ReservationCommandInvoker.getInstance()`

테스트 간 격리를 위해 `@BeforeEach`에서 데이터를 정리합니다.

### 2. 파일 시스템 의존성

테스트는 실제 파일 시스템을 사용합니다:
- 알림 파일: `서버/data/notifications/`
- 로그 파일: `서버/data/logs/`

테스트 종료 시 자동으로 정리되지만, 중단 시 수동 삭제가 필요할 수 있습니다.

### 3. 테스트 순서

`@Order` 어노테이션으로 순서를 지정했지만, 각 테스트는 독립적으로 실행 가능합니다.

---

## 🐛 문제 해결

### 문제 1: "getInstance() method not found"

**원인**: `ReservationCommandInvoker`에 `getInstance()` 메서드가 없음

**해결**: ✅ 이미 수정됨
```java
// ReservationCommandInvoker.java에 이미 수정됨
public static ReservationCommandInvoker getInstance() {
    return instance;
}
```

### 문제 2: 파일 권한 오류

**원인**: `data/` 디렉토리 생성 권한 없음

**해결**:
```bash
# 서버 디렉토리에서 실행
mkdir -p data/notifications
mkdir -p data/logs
chmod 755 data
```

### 문제 3: YAML 파일 관련 오류

**원인**: `reservations.yaml` 파일이 손상되었거나 형식이 잘못됨

**해결**:
```bash
# 기존 YAML 파일 백업 후 삭제
cd 서버/data
mv reservations.yaml reservations.yaml.bak
# 테스트 다시 실행
```

---

## 📈 테스트 커버리지 확인

### JaCoCo로 커버리지 확인 (Maven)

```bash
# pom.xml에 JaCoCo 플러그인 추가 후
mvn clean test jacoco:report

# 리포트 확인
open target/site/jacoco/index.html
```

### 예상 커버리지

- **Command 패키지**: ~90% 이상
- **Observer 패키지**: ~90% 이상
- **전체 프로젝트**: 관련 기능 완전 커버

---

## ✅ 체크리스트

테스트 실행 전 확인사항:

- [x] JUnit 5 의존성 추가 확인
- [x] Lombok 플러그인 설치 확인
- [ ] `data/` 디렉토리 생성 권한 확인
- [x] `ReservationCommandInvoker.getInstance()` 메서드 존재 확인
- [ ] 기존 테스트 데이터 정리

---

## 📞 문의

- **작성자**: 강준화
- **테스트 범위**: SFR-401 ~ SFR-412
- **적용 패턴**: Command 패턴, Observer 패턴

---

## 🎉 결론

이 통합 테스트는 다음을 검증합니다:

1. ✅ **Command 패턴**이 올바르게 구현되었는지
2. ✅ **Observer 패턴**이 올바르게 구현되었는지
3. ✅ **Undo/Redo** 기능이 정상 동작하는지
4. ✅ **알림 시스템**이 정상 동작하는지
5. ✅ **로깅 시스템**이 정상 동작하는지
6. ✅ **통합 시나리오**가 정상 동작하는지

**모든 테스트가 통과하면, SFR-401 ~ SFR-412 요구사항이 완벽히 충족되었음을 의미합니다!** 🎊
