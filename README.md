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

- [x] demo
  - [x] break down demo into pieces
  - [x] show chat app
  - [x] ngrok during demo and share URL for everyone to participate
- [x] makefile to automate everything

## Features

- [x] Webapp
- [x] Chat API
  - [x] Get Messages
  - [x] Create Message
- [x] Storage Service (Redis)
- [x] GIF Service
