# CPU 사용률 수집 및 조회 API
서버의 CPU 사용률을 분 단위로 수집하고 지정한 조건에 따라 조회하는 API 입니다. <br>
macOS, Linux와 같은 유닉스 기반의 운영체제에서 동작합니다.

## 개발 환경

```yml
Java 11
Spring Boot 2.5.8
Spring Data JPA
Gradle 7.3
Swagger 3.0

H2 Database 2.2.224 (로컬 & 개발용)
MariaDB 11.3.2 (운영용)
```

## 프로젝트 설정 및 실행 방법 (macOS 기준)
1. 설정 

    이 Repository를 클론합니다.

    ```bash
    git clone https://github.com/tein408/cpumoitor.git
    ```

2. (데이터베이스가 설치되어 있지 않은 경우) 데이터 베이스 설정

    1) H2 Database 설정

        (1) `http://h2database.com/html/main.html` 에 접속

        ```bash
        http://h2database.com/html/main.html
        ```

        (2) `Download`에서 `Platform-Independent Zip` 클릭하여 다운로드

        (3) 다운로드 받은 `H2 Database` 설치파일 Zip 압축해제

        (4) 설치 확인

        터미널에서 다운로드 받은 `h2` 폴더로 이동 후 `bin` 폴더 내 `h2.sh` 실행
        ```bash
        cd h2/bin
        ./h2.sh &
        ```
        만약 h2.sh파일에 대한 `Permission Error`가 발생한다면 권한 부여
        ```bash
        chmod 755 ./bin/h2.sh
        ```

        (5) H2 Console 웹 화면 확인
        ```
        http://localhost:8080/h2-console/
        ```

    2) MariaDB 설정

        (1) 설치
        ```bash
        brew install mariadb
        ```

        (2) 설치 확인
        ```bash
        mariadb -V
        ```

        (3) 실행
        ```bash
        mysql.server start
        ```

        (4) mariadb 접속
        ```bash
        mariadb
        ```

        (5) 유저 생성
        ```sql
        create user 'cpumonitor'@'127.0.0.1' identified by 'cpumonitor13579';
        ```

        (6) 데이터베이스 생성
        ```sql
        create database cpumonitor;
        ```

        (7) 권한 설정
        ```sql
        GRANT ALL PRIVILEGES ON cpumonitor.* TO 'cpumonitor'@'127.0.0.1';
        FLUSH PRIVILEGES;
        ```

        8. 데이터베이스 확인
        ```sql
        show databases;
        ```

        9. 종료
        ```sql
        \q
        ```

2. 프로젝트 디렉토리로 이동합니다.

    ```bash
    cd cpumonitor
    ```

3. 필요한 의존성을 설치합니다.

    ```bash
    ./gradlew build
    ```

4. 데이터베이스 설정을 수정합니다.

    `application.yml` 파일에서 접속에 필요한 데이터베이스 관련 설정을 수정합니다.
    > 프로젝트 최초 설정을 위해 테이블 생성 옵션이 `ddl-auto: create`로 설정 되어 있습니다. <br>
    이후 운영시에는 `ddl-auto: create` 설정을  `ddl-auto: none`으로 사용해야 합니다. <br>
    `ddl-auto: create`을 사용할 경우 애플리케이션을 매 실행시마다 테이블을 새로 생성하게되어 데이터가 데이터베이스에 누적되지 않습니다.

    ```yml
    spring:
      config:
        activate:
          on-profile: main
      datasource:
        url: jdbc:mariadb://localhost:3306/cpumonitor
        username: cpumonitor
        password: cpumonitor13579
        driver-class-name: org.mariadb.jdbc.Driver
      jpa:
        hibernate:
          ddl-auto: create # 최초 실행시에만 create 사용, 이후는 none 사용
      ```
    

5. 필요한 환경에 맞춰 실행합니다

    ```bash
    # 개발 모드 실행: H2 Database를 사용합니다.
    ./gradlew bootRun -Pprofile=dev

    # 운영 모드 실행: MariaDB를 사용합니다.
    ./gradlew bootRun -Pprofile=main

    # default: 프로필을 입력하지 않은 경우, 개발 모드(dev프로필과 동일)로 실행됩니다.
    ./gradlew bootRun
    ```

6. 접속

    서버가 성공적으로 시작되면 `http://localhost:8080/swagger-ui/index.html`에 액세스할 수 있습니다.
   
    <img width="1450" alt="image" src="https://github.com/tein408/cpumoitor/assets/75615404/68afa23c-e3b2-4726-8997-07746cef6fe6">

## API 문서

`http://localhost:8080/swagger-ui/index.html` 에 접속하여 Swagger로 작성된 API 문서를 확인할 수 있습니다.

### 분 단위 CPU 사용률 데이터 조회
- URL: `/api/cpu-usage/minute`
- Method: GET
- 파라미터:
    - startDateTime (시작 날짜 및 시간): YYYY-MM-DDTHH:MM:SS 형식의 문자열
      (예: 2024-05-01T00:00:00)
    - endDateTime (종료 날짜 및 시간): YYYY-MM-DDTHH:MM:SS 형식의 문자열
      (예: 2024-05-01T23:59:59)
    - 파라미터를 입력하지 않은 경우 최근 1주간 데이터를 조회합니다.
- 성공 응답:
    - Status Code: 200
    - Content: 분 단위 CPU 사용률 데이터 목록
    ```json
    [
        {
            "idleUsage": 0.0,
            "recordedAt": "2024-05-24T16:59:02.201Z",
            "systemUsage": 0.0,
            "userUsage": 0.0
        }
    ]
    ```
    ```java
    "idleUsage": 0.0,                           # idle usage를 나타냅니다.
    "recordedAt": "2024-05-24T16:59:02.201Z",   # 사용률 조회 시간을 나타냅니다. Java의 LocalDateTime 타입을 사용합니다.
    "systemUsage": 0.0,                         # system usage를 나타냅니다.
    "userUsage": 0.0                            # user usage를 나타냅니다.
    ```
- 실패 응답:
    - Status Code: 500
    - Content: 빈 목록

### 시간 단위 CPU 사용률 데이터 조회
- URL: `/api/cpu-usage/hour`
- Method: GET
- 파라미터:
    - date (조회할 날짜): YYYY-MM-DD 형식의 문자열
      (예: 2024-05-01)
    - 파라미터를 입력하지 않은 경우 최근 3달간 데이터를 조회합니다.
- 성공 응답:
    - Status Code: 200
    - Content: 시간 단위 CPU 사용률 데이터 목록

    ```json
    [
        {
            "avgIdleUsage": 0.0,
            "avgSystemUsage": 0.0,
            "avgUserUsage": 0.0,
            "maxIdleUsage": 0.0,
            "maxSystemUsage": 0.0,
            "maxUserUsage": 0.0,
            "minIdleUsage": 0.0,
            "minSystemUsage": 0.0,
            "minUserUsage": 0.0,
            "recordedAt": "string"
        }
    ]
    ```
    ```java
    "avgIdleUsage": 0.0       # idle usage 평균값을 나타냅니다.
    "avgSystemUsage": 0.0     # system usage 평균값을 나타냅니다.
    "avgUserUsage": 0.0       # user usage 평균값을 나타냅니다.
    "maxIdleUsage": 0.0       # idle usage 최댓값을 나타냅니다.
    "maxSystemUsage": 0.0     # system usage 최댓값을 나타냅니다.
    "maxUserUsage": 0.0       # user usage 최댓값을 나타냅니다.
    "minIdleUsage": 0.0       # idle usage 최솟값을 나타냅니다.
    "minSystemUsage": 0.0     # system usage 최솟값을 나타냅니다.
    "minUserUsage": 0.0       # user usage 최솟값을 나타냅니다.
    "recordedAt": "string"    # 기준 시간을 '2024-05-01T12:00' 문자열 형태로 나타냅니다.
    ```

- 실패 응답:
    - Status Code: 500
    - Content: 빈 목록

### 일 단위 CPU 사용률 데이터 조회
- URL: `/api/cpu-usage/day`
- Method: GET
- 파라미터:
    - startDate (조회할 시작 날짜): YYYY-MM-DD 형식의 문자열
      (예: 2024-05-01)
    - endDate (조회할 종료 날짜): YYYY-MM-DD 형식의 문자열
      (예: 2024-05-05)
    - 파라미터를 입력하지 않은 경우 최근 1년간 데이터를 조회합니다.
- 성공 응답:
    - Status Code: 200
    - Content: 일 단위 CPU 사용률 데이터 목록

    ```json
    [
        {
            "date": "string",
            "avgIdleUsage": 0.0,
            "avgSystemUsage": 0.0,
            "avgUserUsage": 0.0,
            "maxIdleUsage": 0.0,
            "maxSystemUsage": 0.0,
            "maxUserUsage": 0.0,
            "minIdleUsage": 0.0,
            "minSystemUsage": 0.0,
            "minUserUsage": 0.0
        }
    ]
    ```
    ```java
    "date": "string"          # 기준 날짜를 '2024-05-01' 문자열 형태로 나타냅니다.
    "avgIdleUsage": 0.0       # idle usage 평균값을 나타냅니다.
    "avgSystemUsage": 0.0     # system usage 평균값을 나타냅니다.
    "avgUserUsage": 0.0       # user usage 평균값을 나타냅니다.
    "maxIdleUsage": 0.0       # idle usage 최댓값을 나타냅니다.
    "maxSystemUsage": 0.0     # system usage 최댓값을 나타냅니다.
    "maxUserUsage": 0.0       # user usage 최댓값을 나타냅니다.
    "minIdleUsage": 0.0       # idle usage 최솟값을 나타냅니다.
    "minSystemUsage": 0.0     # system usage 최솟값을 나타냅니다.
    "minUserUsage": 0.0       # user usage 최솟값을 나타냅니다.
    ```

- 실패 응답:
    - Status Code: 500
    - Content: 빈 목록

## Test Coverage 확인

Test coverage 확인을 위해 `Jacoco`를 사용하였습니다. (ver 0.8.7) <br>
터미널에서 아래의 명령어를 입력한 후 프로젝트 폴더 내의 `cpumonitor/build/jacoco/report.html` 경로에서 `index.html` 을 실행하여 확인할 수 있습니다.

```bash
./gradlew check
```

- Test만 실행하려는 경우: 터미널에서 아래의 명령어를 입력하여 test만 실행시킬 수 있습니다.
```bash
./gradlew test
```

Test coverage 기준은 각각 80% 이상이며, <br> 
현재 Line coverage 100%, Branch coverage 100% 입니다.

<img width="1045" alt="image" src="https://github.com/tein408/cpumoitor/assets/75615404/c64a2d50-f9b3-4f42-bf44-b9e769d72c26">

## CPU 사용률 수집 Scheduler

> CPU의 사용률을 분 단위로 수집하기 위해 스케쥴러를 사용하였습니다. <br>
macOS, Linux와 같은 유닉스 기반의 운영체제에서 동작합니다.

- `@Scheduled(cron = "0 * * * * *")` 를 통해 매 분마다 실행됩니다.
- `ProcessBuilder`를 통해 `top -l 1 | grep -E "^CPU" | awk '{print $3, $5, $7}'` 명령어를 터미널에서 실행하게 됩니다.
    ```bash
    # 실행 명령어
    top -l 1 | grep -E "^CPU" | awk '{print $3, $5, $7}'

    # 실행 결과 예시
    2.23% 5.75% 92.1%
    ```
- 이를 통해 가져온 데이터를 각각 `user_usage`, `system_usage`, `idle_usage`로 데이터베이스에 저장합니다.
- 만약 명령어를 실행시키거나 데이터 베이스에 저장할 때 에러가 발생하는 경우 로그에 저장합니다.
