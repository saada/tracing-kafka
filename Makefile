all: jaeger chatapi webapp
jaeger:
	docker-compose up -d
webapp:
	cd services/webapp && yarn && yarn dev
chatapi:
	cd services/chat-api && ./gradlew build && java -jar build/libs/chat-api-0.1.0.jar
