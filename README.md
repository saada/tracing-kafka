# Tracing Kafka

- What is open tracing?
- Why trace Kafka?

## Setup

```sh
docker-compose up
cd services/chat-api
./gradlew build && java -jar build/libs/chat-0.1.0.jar
```

## Add new service

```sh
mkdir Service{A,B,C,D}
# repeat for each service
cd ServiceA
gradle init --type java-application
```

## TODOs

- [ ] slides
  - [x] mention Container Hive and interest in observability and tracing
- [ ] demo
  - [x] break down demo into pieces
  - [x] show chat app
  - [x] ngrok during demo and share URL for everyone to participate
  - [ ] pass metadata from each user as baggage
- [x] makefile to automate everything

## Features

- [x] Webapp
- [ ] Chat API
  - [ ] Get Messages
  - [x] Create Message
  - [ ] Delete Message
- [ ] Storage Service (Redis)
- [ ] GIF Service
- [ ] NoFucks Service (fuck -> f#*$)
