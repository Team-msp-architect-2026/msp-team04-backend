# 🖥️ MoMent Backend

> Spring Boot 기반 API 서버  
> 교육·돌봄 추천, 예약, 결제, 알림 기능을 담당하는 백엔드 서비스

---

## 📌 Overview

MoMent Backend는 사용자 프로필을 기반으로  
교육·돌봄 프로그램을 추천하고, 예약 및 결제를 처리하는 REST API 서버입니다.

---

## ⚙️ Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot
- **ORM**: JPA, QueryDSL
- **Security**: Spring Security, JWT
- **Database**: PostgreSQL (RDS)
- **Cache**: Redis (ElastiCache)
- **Batch**: Spring Batch
- **Infra**: AWS EKS

---

## 🧩 주요 기능

- 👤 자녀 프로필 관리
- 🎯 맞춤 추천 API
- 🎟️ 예약 처리
- 💳 결제 처리
- 🔔 알림 시스템
- 💬 커뮤니티

---

## 🚀 Getting Started

### 1️⃣ Clone

```bash
git clone https://github.com/your-repo/backend.git
cd backend
```

### 2️⃣ Environment

```bash
cp .env.example .env
```

### 3️⃣ Run

```bash
./gradlew bootRun
```

---

## 📂 Project Structure

```bash
src
 ├── controller
 ├── service
 ├── repository
 ├── domain
 └── config
```

---
🔗 API Documentation

👉 [Wiki 참고](https://github.com/Team-msp-architect-2026/msp-team04-wiki/wiki/API-Specification)

