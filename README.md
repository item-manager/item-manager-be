# 📋가정용 물품 관리 서비스
> 2023.01 ~ 2023.04   
> 백엔드 1명, 프론트 2명이 진행
<br>

## 프로젝트 소개
**가정에서 집안 물건의 보관 위치와 남은 수량을 관리하는 서비스입니다**   
프론트 서버: [service server](http://ycrpark.iptime.org:3000/)   
백엔드 서버: [API server](http://ycrpark.iptime.org:8080/swagger-ui)
<br>

## 기술 스택
<div>
<img src="https://img.shields.io/badge/java 17-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/springboot 2.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/lombok-E34F26?style=for-the-badge&logo=lombok&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/jpa-0769AD?style=for-the-badge&logo=jpa&logoColor=white">
<img src="https://img.shields.io/badge/querydsl-003545?style=for-the-badge&logo=querydsl&logoColor=white">
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white">
<img src="https://img.shields.io/badge/raspberry pi-A22846?style=for-the-badge&logo=raspberrypi&logoColor=white">
<img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?&style=for-the-badge&logo=IntelliJ%20IDEA&logoColor=white">
</div>

## ERD
<img src="https://github.com/item-manager/item-manager-be/assets/17820260/63a1139f-e92c-491a-bf3d-0c88633d7f0a" width="500px" height="300px"/>

## 주요 기능
#### 1. 계정 로그인
- 로그인하여 본인의 물품을 관리한다

#### 2. 보관 장소 관리
- 물품이 보관된 장소를 [장소(방) > 위치(가구)] 두단계로 카테고리화 하여 관리한다
![image](https://github.com/item-manager/item-manager-be/assets/17820260/4f9fc018-65b7-43de-a56f-f446f8dcfba5)
    - 거실에 책장, tv장이 있는 가정

#### 3. 물품 관리
- 물품을 소모품/비품으로 구분하여 조회한다
  - 소모품은 구매/사용 기능과 최근 구매일, 최근 사용일을 조회한다
    ![image](https://github.com/item-manager/item-manager-be/assets/17820260/9142a5ee-7a8a-43c9-874e-f76a6ff71745)
  - 비품은 보관 장소 관리를 명시한다
    ![image](https://github.com/item-manager/item-manager-be/assets/17820260/29d1538a-5ca5-4ca7-acaa-c0063879c3ea)
- 라벨로 물품을 분류, 조회한다   
    <img src="https://github.com/item-manager/item-manager-be/assets/17820260/0037c71d-f1f3-4793-b0fc-a5d7dca37047" width="400px" height="160px"/>

#### 4. 수량 관리
- 구매/사용 기능으로 남은 수량을 관리한다   
    <img src="https://github.com/item-manager/item-manager-be/assets/17820260/8487914d-c516-415d-8c49-b9939eae36c8" width="300px" height="160px"/>
    <img src="https://github.com/item-manager/item-manager-be/assets/17820260/fcd16ca8-4eb5-4edc-aed2-ac7507295193" width="300px" height="160px"/>
- 구매/사용 기록을 표, 그래프로 조회한다   
    <img src="https://github.com/item-manager/item-manager-be/assets/17820260/d442cb69-6eb4-426f-97c9-59f7ae430c20" width="700px" height="250px"/>
