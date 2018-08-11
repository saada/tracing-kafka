# Tracing Kafka

- What is open tracing?
- Why trace Kafka?

## Setup

```sh
docker-compose up
cd ServiceA
./gradlew run
```

## Add new service

```sh
mkdir Service{A,B,C,D}
# repeat for each service
cd ServiceA
gradle init --type java-application
```

## TODOs

[] mention Container Hive and interest in observability and tracing
[] demo
[] break down demo into pieces
[] gradle project with random timeouts
[] helm chart for gradle project
[] multiple deployments of helm charts
[] makefile to automate everything
