all: compose chatapi webapp storage giphy
compose:
	docker-compose up
webapp:
	cd services/webapp && yarn && yarn dev
chatapi:
	cd services/chat-api && ./gradlew build && java -jar build/libs/chat-api-0.1.0.jar
storage:
	cd services/storage-service && ./gradlew run
giphy:
	cd services/giphy-service && ./gradlew run
spark:
	docker-compose run spark