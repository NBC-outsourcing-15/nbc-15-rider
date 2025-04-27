# 🍽 [Spring 6기] 아웃소싱 프로젝트 - 배달 어플리케이션(Delivery)

### 🕰️ 개발 기간
25.04.22 - 25.04.29


### 🚀 프로젝트 개요
이 프로젝트는 유저와 사장님을 위한 배달 서비스 백엔드 시스템입니다.
회원가입부터 주문, 리뷰, 포인트 및 쿠폰 사용까지 배달 서비스 전체 흐름을 구현하였으며, 소셜 로그인(OAuth2) 및 토스 결제 연동으로 실제 서비스 수준의 기능을 목표로 합니다.

### 📦 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot, Spring MVC, Spring Security, JPA, Hibernate |
| Database | MySQL, Redis |
| Infra | Docker |
| Build | Gradle |
| Tools | IntelliJ, Postman, Git |


---


## 👤 사용자 유형별 기능 정리
### 와이어프레임
![](https://velog.velcdn.com/images/tofha054/post/93c38da5-3188-4730-b1b3-9e16a0768400/image.png)


### 공통

#### ✅ 회원 / 인증
- 네이버, 카카오 __소셜 로그인 지원 (OAuth2)__
- JWT 기반 로그인 처리
- 필터를 통한 인증 처리
- 탈퇴 후 ID 재사용 불가 / 복구 불가

---

### 일반 사용자

#### 🏪 가게 / 메뉴
- 키워드 기반 통합 검색 (가게, 메뉴)
- 가게 단건 조회 (메뉴 포함)

#### 🛒 장바구니
- 한 가게 메뉴만 담을 수 있음
- 가게 변경 시 장바구니 초기화
- 마지막 업데이트로부터 24시간 유지

#### 🧾 주문
- 장바구니에서 주문 생성
- 본인의 주문 내역, 단건 조회
- 포인트 사용 가능 (포인트로만 결제 가능)
- **토스 결제 API 연동**을 통한 포인트 충전
- __🔔 알림__ : 주문 상태 변경 시 알림 받기

#### 📝 리뷰
- 리뷰 작성 및 조회, 삭제
- 각 메뉴별 리뷰 작성 가능

---

### 사장님

#### 🏪 가게 관리
- 가게 생성 / 수정 / 폐업
- 가게 카테고리 설정 (한식, 중식, 일식 등)

#### 🍽 메뉴 관리
- 메뉴 생성 / 수정 / 삭제
- 메뉴 카테고리 구성

#### 🧾 주문 관리
- 주문 수락 / 취소 / 완료 처리
- 주문 상세 조회, 가게 주문 전체조회


---

## 🗂 ERD
![](https://velog.velcdn.com/images/tofha054/post/86b54ee3-9e8e-4121-9658-0b08b743e65f/image.png)


---

## 📮 API 명세서
<a href= "https://www.notion.so/teamsparta/JSON-1dd2dc3ef5148060a1cec75d6473cff4?pvs=4"> 명세서 링크 </a>


---
## 👥 팀원 소개

| 이름  | GitHub                                         | 역할                                   |
|-----|------------------------------------------------|--------------------------------------|
| 김정민 | [@Juungmini0601](https://github.com/Juungmini0601)         | 팀장 / 가게 및 메뉴 CRUD, 키워드 기반 통합 검색  |
| 성우영 | [@SW00Y](https://github.com/SW00Y)         | 장바구니, 결제 기능 보조   |
| 정석현 | [@sukh115](https://github.com/sukh115)         | 회원 / 인증기능 (소셜 로그인), 알림 기능|
| 권새롬 | [@Ksr-ccb](https://github.com/Ksr-ccb)         | 장바구니, 결제 기능   |
| 이승현 | [@SeungHyunLee054](https://github.com/SeungHyunLee054) | 포인트 충전( 토스 API), 리뷰 기능  |


---

## 📦 패키지 구조
```
+---java
|   \---rider
|       \---nbc
|           |   Nbc15Application.java
|           |
|           +---domain
|           |   +---cart
|           |   |   +---controller
|           |   |   |       CartController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |   +---request
|           |   |   |   |       CartAddRequestDto.java
|           |   |   |   |       CartUpdateRequestDto.java
|           |   |   |   |
|           |   |   |   \---response
|           |   |   |           CartItemResponseDto.java
|           |   |   |           CartListResponseDto.java
|           |   |   |
|           |   |   +---exception
|           |   |   |       CartException.java
|           |   |   |       CartExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       CartRedisRepository.java
|           |   |   |
|           |   |   +---service
|           |   |   |       CartService.java
|           |   |   |
|           |   |   \---vo
|           |   |           Cart.java
|           |   |           MenuItem.java
|           |   |
|           |   +---menu
|           |   |   +---controller
|           |   |   |       MenuController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |       MenuCreateRequestDto.java
|           |   |   |       MenuResponseDto.java
|           |   |   |       MenuUpdateRequestDto.java
|           |   |   |
|           |   |   +---entity
|           |   |   |       Menu.java
|           |   |   |
|           |   |   +---exception
|           |   |   |       MenuException.java
|           |   |   |       MenuExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       MenuRepository.java
|           |   |   |
|           |   |   \---service
|           |   |           MenuService.java
|           |   |
|           |   +---order
|           |   |   +---controller
|           |   |   |       OrderController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |   +---requestDto
|           |   |   |   |       OrderStatusRequestDto.java
|           |   |   |   |
|           |   |   |   \---responseDto
|           |   |   |           OrderResponseDto.java
|           |   |   |           OrderStatusResponseDto.java
|           |   |   |
|           |   |   +---entity
|           |   |   |       Order.java
|           |   |   |
|           |   |   +---enums
|           |   |   |       OrderStatus.java
|           |   |   |
|           |   |   +---exception
|           |   |   |       OrderException.java
|           |   |   |       OrderExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       OrderRepository.java
|           |   |   |
|           |   |   +---service
|           |   |   |       OrderService.java
|           |   |   |
|           |   |   \---vo
|           |   |           OrderMenu.java
|           |   |
|           |   +---payment
|           |   |   +---constant
|           |   |   |       PaymentConstants.java
|           |   |   |
|           |   |   +---controller
|           |   |   |       PaymentController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |   +---request
|           |   |   |   |       PaymentCancelRequest.java
|           |   |   |   |       PaymentRequest.java
|           |   |   |   |
|           |   |   |   \---response
|           |   |   |           PaymentFailResponse.java
|           |   |   |           PaymentResponse.java
|           |   |   |
|           |   |   +---entity
|           |   |   |       Payment.java
|           |   |   |
|           |   |   +---enums
|           |   |   |       OrderNameType.java
|           |   |   |       PayType.java
|           |   |   |
|           |   |   +---exception
|           |   |   |   |   PaymentException.java
|           |   |   |   |
|           |   |   |   \---code
|           |   |   |           PaymentExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       PaymentRepository.java
|           |   |   |
|           |   |   \---service
|           |   |           PaymentService.java
|           |   |
|           |   +---review
|           |   |   +---controller
|           |   |   |       StoreReviewController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |   +---request
|           |   |   |   |       StoreReviewCreateRequest.java
|           |   |   |   |       StoreReviewUpdateRequest.java
|           |   |   |   |
|           |   |   |   \---response
|           |   |   |           StoreReviewResponse.java
|           |   |   |
|           |   |   +---entity
|           |   |   |       StoreReview.java
|           |   |   |
|           |   |   +---exception
|           |   |   |   |   StoreReviewException.java
|           |   |   |   |
|           |   |   |   \---code
|           |   |   |           StoreReviewExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       StoreReviewRepository.java
|           |   |   |
|           |   |   +---service
|           |   |   |       StoreReviewService.java
|           |   |   |
|           |   |   \---vo
|           |   |           MenuReview.java
|           |   |
|           |   +---store
|           |   |   +---constant
|           |   |   |       StoreConstants.java
|           |   |   |
|           |   |   +---controller
|           |   |   |       StoreController.java
|           |   |   |
|           |   |   +---dto
|           |   |   |       StoreCreateRequestDto.java
|           |   |   |       StoreDetailResponseDto.java
|           |   |   |       StoreResponseDto.java
|           |   |   |       StoreReviewsResponseDto.java
|           |   |   |       StoreUpdateRequestDto.java
|           |   |   |
|           |   |   +---entity
|           |   |   |       OperatingHours.java
|           |   |   |       Store.java
|           |   |   |       StoreAddress.java
|           |   |   |       StoreStatus.java
|           |   |   |
|           |   |   +---exception
|           |   |   |       StoreException.java
|           |   |   |       StoreExceptionCode.java
|           |   |   |
|           |   |   +---repository
|           |   |   |       StoreRepository.java
|           |   |   |
|           |   |   \---service
|           |   |           StoreService.java
|           |   |
|           |   \---user
|           |       +---controller
|           |       |       UserController.java
|           |       |
|           |       +---dto
|           |       |       KakaoUserInfoResponse.java
|           |       |       LoginRequestDto.java
|           |       |       NaverUserInfoResponse.java
|           |       |       ReissueRequestDto.java
|           |       |       SignupRequestDto.java
|           |       |       UpdatePasswordRequestDto.java
|           |       |       UpdateUserRequestDto.java
|           |       |       UpdateUserResponseDto.java
|           |       |       UserResponseDto.java
|           |       |       WithdrawRequestDto.java
|           |       |
|           |       +---entity
|           |       |       Role.java
|           |       |       SocialType.java
|           |       |       User.java
|           |       |       UserStatus.java
|           |       |
|           |       +---exception
|           |       |       UserException.java
|           |       |       UserExceptionCode.java
|           |       |
|           |       +---repository
|           |       |       UserRepository.java
|           |       |
|           |       \---service
|           |               CustomOAuth2UserService.java
|           |               UserService.java
|           |
|           \---global
|               +---aop
|               |       OrderLogAspect.java
|               |
|               +---auth
|               |       AuthUser.java
|               |
|               +---config
|               |       JpaConfig.java
|               |       RedisConfig.java
|               |       RestTemplateConfig.java
|               |       SecurityConfig.java
|               |       SwaggerConfig.java
|               |       TimeBaseEntity.java
|               |
|               +---exception
|               |   |   BaseException.java
|               |   |
|               |   +---dto
|               |   |       ValidationError.java
|               |   |
|               |   \---handler
|               |           GlobalExceptionHandler.java
|               |
|               +---handler
|               |       OAuth2LoginSuccessHandler.java
|               |
|               +---jwt
|               |   |   JwtAuthenticationEntryPoint.java
|               |   |   JwtAuthenticationFilter.java
|               |   |   JwtTokenProvider.java
|               |   |
|               |   +---dto
|               |   |       TokenResponseDto.java
|               |   |
|               |   +---jwtException
|               |   |       JwtAuthenticationException.java
|               |   |       JwtErrorResponse.java
|               |   |       JwtExceptionCode.java
|               |   |
|               |   \---service
|               |           RefreshTokenService.java
|               |
|               +---response
|               |       CommonResponse.java
|               |       CommonResponses.java
|               |
|               \---util
|                       LogUtils.java
|
\---resources
    |   application-local.yml
    |   application.yml
    |
    \---static
            toss.html

```
