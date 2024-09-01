![우테캠-7-ELEVEN-발표자료 (1)-이미지-0](https://github.com/user-attachments/assets/820e0254-c99a-4927-90eb-4fc3a94777e0)

<br><br>

## 📚 프로젝트 소개
> 시간이 지날수록 가격이 떨어지는 경매! 기다리면 더 싸게 살 수 있지만, 그 전에 재고가 없을지도 모릅니다!

우아한테크캠프 7기 7팀 프로젝트 결과물입니다.  짧은 시간 동안 여러 명의 사용자 요청이 몰리는 경매의 특성에 맞게 동시성을 제어하고, 사용자 요청을 신뢰적으로 처리하는 것에 집중하였습니다.

[API-DOCS](https://woowa-techcamp-2024.github.io/Team7-ELEVEN/src/main/resources/docs/index.html)

<br><br>

## 🔖 기능

<p align="center"> 
  <img src="https://github.com/user-attachments/assets/9d7df0c2-a2d3-4d62-b625-e61f5709a8d9" align="center" width="32%">  
  <img src="https://github.com/user-attachments/assets/fea80967-a0c2-4cfe-a0be-e3af5f038613" align="center" width="32%">  
  <img src="https://github.com/user-attachments/assets/8c664bc4-29a3-469f-a02b-9ffd9d759d87" align="center" width="32%">  
  <figcaption align="center">UI</figcaption>
</p>

<br>

### 가격 하락 전략
- 경매 시간은 최대 1시간, 가격 하락주기는 경매 시간에 나누어떨어지는 시간이어야 합니다.
- 따라서 초, 분, 시 모두 하락주기로 선정될 수 있습니다.
- 이를 동적으로 계산하기보다 클라이언트 요청이 왔을 때 정적으로 계산해 주는 방식을 채택했습니다.

<br>

### 경매 목록
> 사용자는 경매 목록을 확인한다.
- 사용자는 해당 페이지를 통해 경매 목록을 확인할 수 있습니다.
- 각 경매는 상품 이름, 시작 시각, 종료 시각, 시작 가격, 현재 상태를 가집니다.
- 시작 시각과 종료 시각을 기준으로 현재 상태와 남은 시간을 사용자에게 출력합니다.

<br>

### 경매 입찰
> 사용자는 경매를 입찰할 수 있다.
- 사용자는 상세 페이지에서 경매 재고와 할인 정책을 확인할 수 있습니다.
- 경매의 가격과 재고는 실시간으로 변화합니다.
- 사용자는 현재 가격을 기준으로 입찰을 진행할 수 있습니다.

<br>

### 구매 내역
> 사용자는 자신이 구매한 내역을 확인한다.
- 사용자는 성공적으로 입찰을 진행한 결과를 내역에 확인할 수 있습니다.
- 각 내역은 거래 단가, 수량 그리고 상태를 가집니다.
- 사용자는 경매가 종료된 이후에 구매 내역 상세 페이지에서 환불을 진행할 수 있습니다.

<br><br>

## 🏛️ 아키텍처

### 서버 아키텍처
**[dev (초기 버전)](https://github.com/woowa-techcamp-2024/Team7-ELEVEN/tree/dev)**
![image](https://github.com/user-attachments/assets/36435b6d-153a-4fb2-abfa-d77d69a8d28c)

**[dev2 (개선 버전)](https://github.com/woowa-techcamp-2024/Team7-ELEVEN/tree/dev2)**
![image](https://github.com/user-attachments/assets/ef57d280-d60d-4e3b-a0af-1d5e8fb7badd)

- EC2 T3.small 3대 (`Spring Boot 3.3.2` 2대, `Redis 7.2.5` 1대)
- RDS T3.small 1대 (`MySQL 8.0.35`)
- ALB

<br>

### 기술 스택
**Language** | Java 17

**Framework** | Spring Boot 3.3.2, Spring Data JPA 3.3.2, QueryDSL 5.1.0

**Database** | MySQL 8.0.35, Redis 7.2.5, H2

**Monitoring** | Prometheus 1.13.2, Grafana

**Build Tool** | Gradle 8.8

**Docs** | Spring REST Docs 3.0.1

<br><br>

## 💁🏻‍♂️ 협업 전략

### 그라운드 룰
- 열린 의사소통: 하고 싶은 말이 있으면 바로 이야기하기!
- 일일 스크럼: 매일 오전 10시에 진행 (오늘의 기분, 어제 진행한 업무, 금일 진행할 업무)

<br>

### 브랜치 전략

| dev  | 초기 개발 버전 |
|:----:|:--------:|
| dev2 |  개선 버전   |
| feat/{issue-number} |  기능 구현   |
|refactor/{issue-number} |   리펙토링   |
|bug/{issue-number} |  버그 수정   |
|docs/{issue-number} |    문서    |
|chore/{issue-number} |    기타    |

<br>

### Merge 전략

- 코드 리뷰: 최소 1명 이상의 승인 필요
- 자동화된 테스트: CI/CD 파이프라인의 모든 테스트 통과 필수

<br>

### 도메인 이해: 비즈니스 로직 집중하기
- 팀 내 경매 도메인 전문성 부족을 인식했습니다.
- 이를 해결하기 위해, 프레임워크 의존성이 없는 순수 도메인 모델과 비즈니스 로직에 집중하는 환경에서 프로젝트를 시작했습니다.
- 기술적인 부분을 배제하고 개발을 진행하여, 팀 전체가 경매 도메인의 핵심 개념을 빠르게 이해할 수 있었습니다.

<br>

### 스토리 기반의 태스크 분할

<p align="center"> 
  <img src="https://github.com/user-attachments/assets/70edab63-fa11-4b1c-9bac-33a6de277330" align="center" width="30%">  
  <img src="https://github.com/user-attachments/assets/0c88acf1-1f6d-4bdd-bef7-ed7ce59d198e" align="center" width="69%">  
</p>

- **WHY? (적용한 이유)**
  - 목적: 작업 명목 해소, 효율적인 커뮤니케이션, 태스크 간 의존성 파악 및 우선순위 결정
- **HOW? (적용 방법)**
  - 기능 단위로 사용자 스토리 작성
  - 스토리를 구현 가능한 최소 단위의 태스크로 분할
  - 테스크 간 의존성 파악 및 우선순위 결정
  - Github Project를 통해 백로그와 이슈를 관리
- **효과**
  - 변경 점이 작아 리뷰가 쉬워지고 코드 품질을 일정하게 유지할 수 있음
  - Task가 작아 작업에 대한 부담이 작아지고, 컨텍스트에 대한 공유가 쉬움
  - 상호간 빠른 피드백을 줄 수 있음
  - 작은 Task를 기반으로 스프린트 주기를 더 짧게 가져갈 수 있음


<br><br>

## 🤔 기술적 고민

### 🔐 경매 입찰에서의 동시성 문제 발견

- 경매 입찰 로직을 시작할 때, 재고 감소, 사용자 포인트 증감 등 여러 로직이 얽혀있었고, 동시성 테스트를 진행했을 때 정합성이 깨지는 걸 발견
- 재고와 포인트에 `Lock`을 추가하여 데이터 정합성 문제를 해결


<details>
<summary>자세히보기</summary>

**문제 상황**

- 사용자가 구매하면 즉시 경매 재고가 차감, 판매자의 포인트가 증가하게 됩니다.
- 여러 명의 사용자가 동시에 같은 경매 입찰을 요청하는 경우 재고가 정상적으로 반영되지 못하게 됩니다.
- 최악의 경우 제한된 물건의 양보다 더 많은 구입이 발생하고, 판매자는 제대로 돈을 정산받지 못하게 됩니다.

**해결 방안**
- 경매 재고, 판매자 구매자의 포인트 등 여러 곳에 락이 필요하므로 `Redisson Lock`을 이용해 분산락을 구현하여 해결함

**아쉬운 점**
- 문제를 해결하기 위한 여러 가지 Lock이 존재하는데 비교해 보지 못하고 바로 Redis Lock을 적용한 점이 아쉽습니다.

</details>

<br>

### ⌛️ 적절한 Lock Waiting Time & Lease Time 선택하기

- 락을 적용 한 후 입찰 로직의 평균 소요 시간은 6ms라는 것을 확인 했지만, 부하 테스트를 진행하니 실패율이 높아짐
- 테스트 결과를 기반으로 최적의 `Wait Time`과 `Lease Time`을 재조정 하여 문제를 해결함

<details>
<summary>자세히보기</summary>

**문제 상황**
- 사용자 요청이 몰리는 상황에서 `Lock`의 획득을 기다리는 사용자들이 많았고 그에 비해 `Wait Time`은 너무 짧아 입찰 시도조차 못 하는 문제가 발생했습니다.
- 외부 네트워크 요청이 필요한 입찰 로직 특성상 충분한 `Lease Time`이 필요했는데, 할당된 시간이 너무 짧아 작업을 전부 처리하지 못하고 오류가 발생했습니다.

<p align="center"> 
  <img src="https://github.com/user-attachments/assets/a175b566-78f4-442f-9e3c-5185e3a8bcdb" align="center" width="49%">  
  <img src="https://github.com/user-attachments/assets/02e9a799-9298-4b0f-bd70-bcfb7c183e4a" align="center" width="49%">  
  <figcaption align="center">LockTime 문제</figcaption>
</p>

**해결 방안**
- `Wait Time` 설정
  - 평균 로직 실행 시간 6ms로 측정
    락 대기 시간 = (스레드 * 평균 실행 시간 * 안전계수)
    = 200 * 6ms * 1.5
    = 1,800ms ≈ (2초)
- `Lease Time` 설정
  - 평균 로직 수행시간 6ms로 측정
    넉넉한 락 유지 시간을 위하여
    락 유지 시간 = (평균 실행 시간 * 10)
    = 60ms

</details>

<br>

### 💰 분산락과 트랜잭션을 함께 사용했을 때 원자성을 보장하기
- 경매 입찰 시 재고와 포인트는 차감됐지만, 간헐적으로 경매 입찰에 실패하는 현상을 발견
- 분산락의 유효시간보다 트랜잭션 실행시간이 길게 되어 임계구역이 공유됐던 것이 원인이었고 트랜잭션 타임아웃을 통해 해결함

<details>
<summary>자세히보기</summary>

**문제 상황**
- 입찰 로직 진행 중 `Lock`의 `Lease Time`을 초과하여 `Lock`을 중도 반환함
- `트랜잭션 Commit` 이후 `Lock`을 `Unlock` 하는 시점에서 이미 `Lock`이 반환되었다는 예외가 발생함
- 예외가 발생했을 때 상위 레이어에서 실패 영수증을 생성하는 로직이 동작하여 `성공 영수증 -> 실패 영수증`으로 덮어씌워지는 버그가 발생함
- `Redisson Lock`의 이른 해제로 인해 임계 영역을 서로 다른 요청이 공유하게 되어 동시성 문제가 발생할 수 있음

**해결 방안**
- 커스텀 트랜잭션 타이머를 추가하고, 로직 종료 시점에 해당 타이머 시간을 초과했는지 확인하는 로직을 추가하여 문제를 해결
  - 타임아웃이 발생하는 경우 예외를 발생시키고 트랜잭션을 롤백함
  - 이를 통해 트랜잭션의 진행 시간이 `Lock`의 `Lease Time`보다 짧게 만들어 데이터 정합성을 보장함

<p align="center"> 
  <img src="https://github.com/user-attachments/assets/da0c8592-87a6-4501-9434-109c83a59acc" align="center" width="49%">  
  <img src="https://github.com/user-attachments/assets/ecb2d8a9-aadd-464b-b4c2-5da88482d561" align="center" width="49%">  
  <figcaption align="center">문제 상황</figcaption>
</p>


- 트랜잭션 타임아웃을 설정하여 해결한 모습
  ![image](https://github.com/user-attachments/assets/8bbfaa5b-c7a0-4afd-8bec-7bea5a28c88a)

</details>

<br>

### 🚪 아키텍처 변경을 통한 Throughput 개선
- 경매 특성상 가격이 떨어지는 시점에 입찰 요청이 몰릴 것으로 예상하여 순간적인 부하가 높을 것으로 예상이 됐고, 부하 테스트를 진행함
- 기존 아키텍처는 모든 입찰 요청을 동기로 처리하고 있어서 부하가 높아질 경우 더 늦은 응답을 보내는 걸 테스트 결과를 통해 발견
- 따라서 경매 입찰 요청 처리를 비동기 I/O로 분리하여 사용자에게 빠른 응답을 보장할 수 있도록 개선함

<details>
<summary>자세히보기</summary>

**문제 상황**
- 입찰 처리 로직은 경매, 사용자 포인트 등에 분산락이 적용된 동기 방식으로 동작하고 있었음
- 모든 입찰 요청을 동기로 처리하고 있어 부하가 높아질 경우 더 늦은 응답을 보냄
- 동기 방식의 문제는 사용자가 몰리는 상황에서 `Lock`을 획득하기 위해 여러 사용자(Connection)가 `Blocking` 상태에서 서로 `Lock`을 얻기 위해 대기하기 때문에 요청 처리가 지연됨
- 또한 사용자 요청이 많아도 Lock 대기 시간 때문에 처리할 수 있는 사용자가 한계가 있어 CPU 사용률은 저조한 모습을 보임
  ![image](https://github.com/user-attachments/assets/c8d80087-5a22-484f-95a6-4b61170fe9e3)


**해결 방안**
- 입찰 로직은 다수의 `DB I/O` 작업을 포함한 무거운 작업이었기에 별도의 비동기 서버로 분리함
- 입찰 메시지를 `Redis Stream`에 적재하고 이후 `Consumer 서버`에서 비동기로 메시지를 이용해 `DB I/O` 작업을 처리하도록 개선함
  - 사용자 입찰 요청이 들어오면 입찰 내역에 대한 `UUID`를 생성함, `UUID`는 결과 조회(클라이언트)나 메시지 처리(서버)에서 공통으로 사용하고 있으므로 이를 기준으로 클라이언트는 비동기 요청 처리가 완료되었는지 확인할 수 있음
<p align="center"> 
  <img src="https://github.com/user-attachments/assets/35ef96b9-98c5-4ba1-a122-e38cc610ecbb" align="center" width="49%">  
  <img src="https://github.com/user-attachments/assets/3e76da02-c3c0-4e65-9e99-8016885b7a99" align="center" width="49%">  
  <figcaption align="center">아키텍처 개선 전 후 비교</figcaption>
</p>

</details>

<br><br>

## 😊 프로젝트 팀원
| 이호석 | 유동근 | 오민석 | 최현식 | 
|:---:| :---: | :---: | :---: |
|<a href="https://github.com/HiiWee"><img src="https://avatars.githubusercontent.com/u/66772624?v=4" width="90px" height="90px"> | <a href="https://avatars.githubusercontent.com/u/67232422?v=4"><img src="https://avatars.githubusercontent.com/u/67232422?v=4" width="90px" height="90px"/></a>| <a href="https://github.com/minseok-oh"><img src="https://avatars.githubusercontent.com/u/68336833?v=4" width="90px" height="90px"/></a>| <a href="https://github.com/chhs2131"><img src="https://avatars.githubusercontent.com/u/10378777?v=4" width="90px" height="90px"/></a>|
