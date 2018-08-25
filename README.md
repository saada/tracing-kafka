# Tracing Kafka

- What is open tracing?
- Why trace Kafka?

## Setup

```sh
docker-compose up
cd services/ChatWebApp
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

* [] slides
  * [] mention Container Hive and interest in observability and tracing
  * [] mention alternatives for instrumentation (import company wide library)
* [] demo
  - [x] break down demo into pieces
  * [] show chat app
  * [] ngrok during demo and share URL for everyone to participate
  * [] pass metadata from each user as baggage
* [] gradle project with random timeouts
* [] helm chart for gradle project
* [] makefile to automate everything
