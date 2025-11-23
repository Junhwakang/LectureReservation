# ⭐⭐⭐ Singleton 패턴 구현 - AccountManager ⭐⭐⭐

## 개요

로그인/회원가입 파트에서 **Singleton 패턴**을 주 패턴으로 사용하여 구현했습니다.

## Singleton 패턴이 적용된 클래스

### 서버 측

1. **AccountManager** (`서버/src/main/java/deu/service/AccountManager.java`)
   - **Singleton 패턴**: Thread-safe Lazy Initialization
   - **역할**: 사용자 계정 관리, 로그인/회원가입 처리
   - **특징**: 
     - `synchronized getInstance()` 메서드로 멀티스레드 안전성 보장
     - 사용자 데이터를 메모리에 캐시하여 성능 향상
     - `users.txt` 파일로 데이터 영구 저장

2. **AccountManagerController** (`서버/src/main/java/deu/controller/business/AccountManagerController.java`)
   - **Singleton 패턴**: Eager Initialization
   - **역할**: AccountManager의 Singleton 인스턴스를 사용하여 요청 처리
   - **특징**: 클래스 로딩 시 인스턴스 생성

### 클라이언트 측

3. **AccountManagerClientController** (`클라이언트/src/main/java/deu/controller/business/AccountManagerClientController.java`)
   - **Singleton 패턴**: Thread-safe Lazy Initialization
   - **역할**: 서버의 AccountManager와 통신
   - **특징**: 클라이언트 전체에서 단일 인스턴스만 사용

## Singleton 패턴 구현 방식

### 1. Thread-safe Lazy Initialization (AccountManager, AccountManagerClientController)

```java
private static AccountManager instance;

public static synchronized AccountManager getInstance() {
    if (instance == null) {
        instance = new AccountManager();
    }
    return instance;
}
```

**장점:**
- 최초 호출 시에만 인스턴스 생성 (메모리 효율)
- `synchronized`로 멀티스레드 환경에서 안전

### 2. Eager Initialization (AccountManagerController)

```java
private static final AccountManagerController instance = new AccountManagerController();

public static AccountManagerController getInstance() {
    return instance;
}
```

**장점:**
- 클래스 로딩 시 인스턴스 생성 (빠른 접근)
- JVM이 클래스 로딩을 동기화하므로 멀티스레드 안전

## Singleton 패턴 사용 이유

1. **전역 단일 인스턴스 보장**: 애플리케이션 전체에서 하나의 AccountManager만 존재
2. **메모리 효율성**: 사용자 데이터를 메모리에 캐시하여 파일 I/O 최소화
3. **일관성**: 여러 컨트롤러에서 동일한 AccountManager 인스턴스를 공유
4. **상태 관리**: 로그인한 사용자 목록을 단일 인스턴스에서 관리

## 사용 예시

### 서버 측

```java
// AccountManager의 Singleton 인스턴스 사용
AccountManager accountManager = AccountManager.getInstance();
BasicResponse response = accountManager.login(loginRequest);
```

### 클라이언트 측

```java
// AccountManagerClientController의 Singleton 인스턴스 사용
AccountManagerClientController client = AccountManagerClientController.getInstance();
BasicResponse response = client.login(id, password);
```

## 파일 구조

```
서버/
├── src/main/java/deu/
│   ├── service/
│   │   └── AccountManager.java          ⭐ Singleton 패턴
│   └── controller/business/
│       └── AccountManagerController.java ⭐ Singleton 패턴
└── data/
    └── users.txt                        (AccountManager가 사용하는 데이터 파일)

클라이언트/
└── src/main/java/deu/
    ├── controller/
    │   ├── business/
    │   │   └── AccountManagerClientController.java ⭐ Singleton 패턴
    │   └── event/
    │       └── AccountManagerAuthSwingController.java (Singleton 인스턴스 사용)
    └── view/custom/
        └── ComboBoxRound.java           (역할 선택 UI 컴포넌트)
```

## 주요 기능

- ✅ **로그인**: Singleton 패턴을 통한 사용자 인증
- ✅ **회원가입**: Singleton 패턴을 통한 사용자 등록
- ✅ **로그아웃**: Singleton 패턴을 통한 세션 관리
- ✅ **역할 관리**: ID 접두사(s/p/m)에 따른 자동 역할 할당

## 디자인 패턴 요약

**주 패턴**: ⭐⭐⭐ **Singleton 패턴** ⭐⭐⭐

**기타 구조적 요소** (Singleton 패턴과 함께 사용):
- MVC 구조: Model(AccountManager), View(Auth), Controller(AccountManagerAuthSwingController)
- 이벤트 리스너: Swing의 기본 메커니즘
- 라우팅 로직: SystemController에서 명령 분기

---

**작성일**: 2025-11-22  
**작성자**: AccountManager Team

