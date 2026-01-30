# 📂 FLOTI 리포지토리

**FLOTI** 프로젝트의 통합 개발 리포지토리입니다.
이 문서는 프로젝트 초기 설정 및 실행 방법을 안내합니다.

## 🛠️ 기술 스택 (Tech Stack)

### Frontend
- **Framework**: React Native (Expo)
- **Language**: TypeScript
- **Package Manager**: npm

### Backend
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Database**: MySQL (Main), H2 (Test), Redis (Cache/Session)
- **Build Tool**: Gradle

---

## 🚀 시작하기 (Getting Started)

### 1. 사전 준비 (Prerequisites)
아래 프로그램들이 설치되어 있어야 합니다.
- **Node.js** (LTS 버전 권장)
- **Java JDK 17**
- **MySQL** & **Redis** (백엔드 구동 시 필요)

### 2. 백엔드 실행 (Backend)
`backend/floti-api` 디렉토리로 이동하여 실행합니다.

```bash
# 디렉토리 이동
cd backend/floti-api

# 실행 (Windows)
./gradlew bootRun

# 실행 (Mac/Linux)
./gradlew bootRun
```

> **Note**: 실행 전 `src/main/resources/application.yml` (또는 properties) 파일에서 MySQL 및 Redis 연결 정보를 본인 환경에 맞게 수정해야 할 수 있습니다.

### 3. 프론트엔드 실행 (Frontend)
`frontend/flotiApp` 디렉토리로 이동하여 실행합니다.

```bash
# 디렉토리 이동
cd frontend/flotiApp

# 의존성 설치 (최초 1회)
npm install

# 앱 실행
npm start
```

---

## 📁 주요 디렉토리 구조
- `frontend/flotiApp`: React Native 앱 소스 코드
- `backend/floti-api`: Spring Boot API 서버 소스 코드