# Tracing Kafka

## Setup

```sh
# terminal 1
make compose
# terminal 2
make webapp
# terminal 3
make chatapi
# terminal 4
make storage
# terminal 5
make giphy
# to generate directed graph manually
make spark
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
