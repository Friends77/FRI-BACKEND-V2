# FRI-BACKEND

이 프로젝트는 Docker Compose를 사용하여 백엔드 서버를 실행합니다.

## 요구 사항
- Docker: [설치 가이드](https://docs.docker.com/get-docker/)
- Docker Compose: Docker에 기본 포함되어 있습니다.

## 설정 파일 구성
`docker-compose.yml` 파일은 PostgreSQL, Redis, Spring Boot 서버를 포함한 멀티 컨테이너 애플리케이션을 정의합니다. 

PostgreSQL과 Redis는 Spring Boot 애플리케이션과 연결되며, Docker Compose를 통해 모든 서비스가 동시에 시작됩니다.

## 실행 방법

1. 루트 디렉토리에 있는 docker-compose.yml 을 다운받아주세요 (전체 코드 파일을 다운받을 필요 없고 해당 한 파일만 다운로드 받으시면 됩니다.)

2. docker를 실행해주세요. `docker ps` 가 실행되면 (실행 중인 docker container 를 확인하는 명령어 입니다.) 명령이 터미널에서 문제 없이 실행된다면 docker 가 켜져있는 것입니다.

3. 터미널에서 주소를 docker-compose.yml 를 다운로드 받은 위치로 이동해주세요

4. `docker compose up -d` 명령어를 통해 서버를 띄워주세요.

5. 종료하실 때는 `docker compose down` 을 실행시켜주시면 됩니다.


## 도커가 비정상적으로 종료되어 다시 실행되지 않는 상황인 경우
`docker container prune` 을 입력하고 이후 y/N 을 묻는 질문에 y를 입력해주세요.

이는 모든 종료된 컨테이너를 삭제하는 명령어로 일종의 초기화라고 생각하시면 됩니다.

그럼에도 불구하고 에러가 발생한 경우 디스코드를 통해 따로 물어봐주세요!!
