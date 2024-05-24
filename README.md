# CPU 사용률 수집 및 조회 API
서버의 CPU 사용률을 분 단위로 수집하고 지정한 조건에 따라 조회하는 API 입니다.

## 프로젝트 설정 및 실행 방법
1. 설정 

    이 Repository를 클론합니다.

    ```bash
    git clone https://github.com/tein408/cpumoitor.git
    ```

2. (데이터베이스가 설치되어 있지 않은 경우) 데이터 베이스 설정

    1) H2 Database 설정

        (1) 설치
        `http://h2database.com/html/main.html` 에 접속

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
        ```bash
        create user 'cpumonitor'@'127.0.0.1' identified by 'cpumonitor13579';
        ```

        (6) 데이터베이스 생성
        ```bash
        create database cpumonitor;
        ```

        (7) 권한 설정
        ```bash
        GRANT ALL PRIVILEGES ON cpumonitor.* TO 'cpumonitor'@'127.0.0.1';
        FLUSH PRIVILEGES;
        ```

        8. 데이터베이스 확인
        ```bash
        show databases;
        ```

        9. 종료
        ```bash
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

4. 데이터베이스 설정을 수정합니다. (선택사항)

    `application.yml` 파일에서 접속에 필요한 데이터베이스 관련 설정을 수정합니다.

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
- 성공 응답:
    - Status Code: 200
    - Content: 분 단위 CPU 사용률 데이터 목록
    ```json
    [
        {
            "idleUsage": 0,
            "recordedAt": "2024-05-24T16:59:02.201Z",
            "systemUsage": 0,
            "userUsage": 0
        }
    ]
    ```
    ```java
    "idleUsage": 0,                             # idle usage를 나타냅니다
    "recordedAt": "2024-05-24T16:59:02.201Z",   # 사용률 조회 시간을 나타냅니다
    "systemUsage": 0,                           # system usage를 나타냅니다
    "userUsage": 0                              # user usage를 나타냅니다
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
- 성공 응답:
    - Status Code: 200
    - Content: 시간 단위 CPU 사용률 데이터 목록

    ```json
    [
        {
            "avgIdleUsage": 0,
            "avgSystemUsage": 0,
            "avgUserUsage": 0,
            "maxIdleUsage": 0,
            "maxSystemUsage": 0,
            "maxUserUsage": 0,
            "minIdleUsage": 0,
            "minSystemUsage": 0,
            "minUserUsage": 0,
            "recordedAt": "string"
        }
    ]
    ```
    ```java
    "avgIdleUsage": 0       # idle usage 평균을 나타냅니다
    "avgSystemUsage": 0     # system usage 평균을 나타냅니다
    "avgUserUsage": 0       # user usage 평균을 나타냅니다
    "maxIdleUsage": 0       # idle usage 최댓값을 나타냅니다
    "maxSystemUsage": 0     # system usage 최댓값을 나타냅니다
    "maxUserUsage": 0       # user usage 최댓값을 나타냅니다
    "minIdleUsage": 0       # idle usage 최솟값을 나타냅니다
    "minSystemUsage": 0     # system usage 최솟값을 나타냅니다
    "minUserUsage": 0       # user usage 최솟값을 나타냅니다
    "recordedAt": "string"  # 조회시간을 '2024-05-01T12:00' 문자열 형태로 리턴합니다
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
- 성공 응답:
    - Status Code: 200
    - Content: 일 단위 CPU 사용률 데이터 목록

    ```json
    [
        {
            "date": "string",
            "avgIdleUsage": 0,
            "avgSystemUsage": 0,
            "avgUserUsage": 0,
            "maxIdleUsage": 0,
            "maxSystemUsage": 0,
            "maxUserUsage": 0,
            "minIdleUsage": 0,
            "minSystemUsage": 0,
            "minUserUsage": 0
        }
    ]
    ```
    ```java
    "date": "string"        # 조회 날짜를 '2024-05-01' 문자열 형태로 리턴합니다
    "avgIdleUsage": 0       # idle usage 평균을 나타냅니다
    "avgSystemUsage": 0     # system usage 평균을 나타냅니다
    "avgUserUsage": 0       # user usage 평균을 나타냅니다
    "maxIdleUsage": 0       # idle usage 최댓값을 나타냅니다
    "maxSystemUsage": 0     # system usage 최댓값을 나타냅니다
    "maxUserUsage": 0       # user usage 최댓값을 나타냅니다
    "minIdleUsage": 0       # idle usage 최솟값을 나타냅니다
    "minSystemUsage": 0     # system usage 최솟값을 나타냅니다
    "minUserUsage": 0       # user usage 최솟값을 나타냅니다
    ```

- 실패 응답:
    - Status Code: 500
    - Content: 빈 목록

