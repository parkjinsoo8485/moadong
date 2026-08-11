# 모아동(moadong) 프로젝트 개발 & 서버 배포 가이드 문서

본 문서는 `moadong` 프로젝트의 로컬 서버 실행, 60개 풍부한 더미 데이터 자동 초기화 작업 내역 및 클라우드 무료 서버 배포 플랜 대화 내용을 정리한 기록입니다.

---

## 1. 로컬 개발 환경 실행 내역

* **백엔드 (Spring Boot 3.3.8 / Java 17)**
  * 경로: `backend/`
  * 실행 명령: `.\gradlew.bat bootRun`
  * 서비스 포트: `http://localhost:8080`
* **프론트엔드 (React + Vite + TypeScript)**
  * 경로: `frontend/`
  * 실행 명령: `npm run dev`
  * 서비스 포트: `http://localhost:3000`
* **브라우저 검증**: `http://localhost:3000/` 정상 작동 및 API 데이터 연동 확인 완료

---

## 2. 서버 더미 데이터 강화 및 GitHub 저장 내역

로컬 DB 및 최초 백엔드 구동 시 60개 동아리에 대해 상세하고 풍부한 데이터가 자동으로 생성/업데이트되도록 백엔드 코드를 개편하고 GitHub `main` 브랜치에 저장했습니다.

* **수정 파일**: [DummyDataInitializer.java](file:///c:/Myproject/Opencal/moadong/backend/src/main/java/moadong/global/config/DummyDataInitializer.java)
* **주요 포함 데이터**:
  1. **6개 카테고리 (총 60개 동아리)**: 봉사(10), 학술(10), 공연(10), 운동(10), 취미교양(10), 종교(10)
  2. **상세 내용**: 동아리 소개(`intro`), 활동 내용(`activity`), 수상 실적(`ClubAwardDto`), 인재상 및 인재상 태그(`ClubIdealCandidateDto`), 혜택(`benefits`), FAQ 질문/답변(`FaqDto`)
  3. **모집 & 소셜 정보**: Instagram/YouTube 링크, 모집 기간(`2026-03-01` ~ `2026-12-31`), 지원 폼 URL, 모집 상태(`OPEN` / `RECRUITING`) 자동 계산
* **검증 & 커밋**:
  * 단위/통합 테스트 완료 (`./gradlew.bat test` BUILD SUCCESSFUL)
  * Git 커밋: `feat: 60개 동아리 상세 더미 데이터 자동 초기화 로직 추가` (`e952e3ab`)
  * GitHub 원격 저장소 푸시 완료 (`origin/main`)

---

## 3. 백엔드/프론트엔드 무료 서버 배포 방안 비교

### 3.1. 배포 구조 (통합 vs 분리 배포)

| 구분 | 한 서버 통합 배포 (Monolith / Nginx) | 프론트/백엔드 분리 배포 |
| :--- | :--- | :--- |
| **방식** | 1대 VM에 Spring Boot + React(Nginx) 탑재 | 프론트엔드는 Vercel, 백엔드는 Render 등에 각각 배포 |
| **CORS** | **동일 도메인 사용으로 CORS 에러 없음** | **CORS 허용 설정(`allowedOrigins`) 필수** |
| **속도/관리** | 단일 서버 관리 | Vercel의 글로벌 초고속 CDN 활용, automatic CI/CD |

---

### 3.2. Firebase vs Supabase 서비스 비교

* **Firebase / Supabase (BaaS)**:
  * Spring Boot JAR 애플리케이션을 직접 실행하는 서버(VM)가 아닙니다.
  * DB, 인증, 스토리지 등을 API로 제공하므로 Spring Boot 백엔드를 완전 대체하려면 기존 백엔드 코드를 파기해야 합니다.
  * **Firebase의 활용**: 프론트엔드 웹사이트(React) 무료 호스팅 및 **FCM(푸시 알림)** 전송용으로 이미 연동되어 있어 유용합니다.

---

## 4. 최종 무료 배포 추천 플랜

### 🥇 1순위 추천: **Vercel + Render + Cloud DB (신용카드 없이 빠른 배포)**

* **프론트엔드**: [Vercel](https://vercel.com/) (React / Vite 100% 무료 배포 & 깃허브 푸시 시 자동 배포)
* **백엔드**: [Render.com](https://render.com/) (Spring Boot Web Service 무료 플랜)
* **데이터베이스**: [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) M0 Free (512MB)
* **캐시/MQ**: [Upstash Redis](https://upstash.com/) Free + [CloudAMQP](https://www.cloudamqp.com/) Free
* **특징**: **신용카드 등록 없이 0원으로 즉시 구동 가능**

---

### 🚀 2순위 추천: **Oracle Cloud Always Free (24시간 365일 실서버 가동)**

* **스펙**: ARM Ampere A1 Compute (**4 OCPU, 24GB RAM, 200GB SSD**)
* **구성**: 1대 서버에 `docker-compose`로 Spring Boot + Nginx + MongoDB + Redis + RabbitMQ 몽땅 구동
* **비용 & 과금 안전성**:
  * **평생 0원 (Always Free)**
  * 가입 시 신원 확인용 1달러 결제 후 즉시 환불됨
  * 30일 후 'Free Only' 계정으로 전환되어 **본인이 유료 업그레이드를 누르지 않는 한 어떠한 경우에도 자동 과금되지 않음** (유료 자원 생성 시도 시 결제가 아니라 에러 창 출력)
  * **회수 방지**: Docker 기반으로 복합 컨테이너를 구동하면 유휴(Idle) 회수 정책(CPU 20% 미만 7일 지속) 걱정 없이 24시간 실시간 운영 가능
