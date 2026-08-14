# 🚀 모아동(Moadong) 배포 및 서버 구동 현황 Guide

본 문서는 모아동 프로젝트의 현재 백엔드/프론트엔드/데이터베이스/터널 구동 현황 및 운영 주의사항을 정리한 문서입니다.

---

## 🟢 1. 실행 중인 서버 및 배포 목록

| 구분 | 역할 및 기술 스택 | 접속 URL / 주소 | 구동 방식 | 비고 |
| :--- | :--- | :--- | :--- | :--- |
| **프론트엔드 배포** | Netlify Production Build | [https://beamish-chebakia-c23511.netlify.app/](https://beamish-chebakia-c23511.netlify.app/) | Netlify CDN Cloud | 24시간 항시 접속 가능 |
| **백엔드 외부 터널** | LocalTunnel Proxy Server | [https://moadong-backend.loca.lt](https://moadong-backend.loca.lt) | 로컬 터널링 (`task-776`) | 로컬 PC 구동 시 유효 |
| **백엔드 API** | Spring Boot 3.3.8 (Java 17) | [http://localhost:8080](http://localhost:8080) | 로컬 백그라운드 프로세스 | 30개 동아리 초기화 |
| **DB & DB 초기화** | Embedded MongoDB | Spring Boot 내장 메모리 | 백엔드 내장 인메모리 | 30개 풍부한 더미 데이터 |
| **캐시 서버** | Local Fake Redis Server | `127.0.0.1:6379` | 로컬 Python 백그라운드 | `local-fake-redis.py` 구동 |
| **프론트엔드 로컬** | React + Vite Dev Server | [http://localhost:3000](http://localhost:3000) | 로컬 개발 서버 | 개발 테스트용 |

---

## ⚠️ 2. 접속 및 구동 주의사항

1. **프론트엔드 웹사이트 (Netlify)**
   - Netlify 클라우드 CDN에 정적 웹 파일이 배포되어 있어 **컴퓨터를 꺼도 웹 페이지 접속은 24시간 항상 가능**합니다.

2. **백엔드 API 및 DB 데이터 연동**
   - 백엔드(Spring Boot, DB, Redis) 및 외부 터널(`localtunnel`)이 **사용자 로컬 PC**에서 동작 중입니다.
   - 따라서 **PC 전원이 꺼지거나 백그라운드 프로세스가 종료되면**, 프론트엔드 화면은 켜지지만 동아리 목록 조회/지원하기 등의 API 데이터 연동은 동작하지 않거나 에러가 발생합니다.

3. **LocalTunnel 특성**
   - PC 재부팅 후에는 백엔드 프로세스 및 localtunnel을 재구동해야 합니다.

---

## 💡 3. 24시간 완전 무중단 운영 전환 방법

컴퓨터 전원 상태와 상관없이 24시간 데이터까지 완전하게 무중단 운영하려면 아래 클라우드 환경 이전을 권장합니다:
- **백엔드(Spring Boot)**: Render / Railway / Fly.io / AWS 등의 클라우드 서버 배포
- **데이터베이스**: MongoDB Atlas 클라우드 DB 연동
