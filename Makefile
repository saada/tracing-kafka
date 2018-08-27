all: jaeger chatapi webapp
jaeger:
	docker-compose up -d
webapp:
	cd services/webapp && yarn && yarn dev
chatapi:
	cd services/ChatWebApp && ./gradlew build && java -jar build/libs/chat-0.1.0.jar
