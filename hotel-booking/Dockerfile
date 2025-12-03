FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/hotel-booking.jar hotel-booking.jar
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hotel_booking
ENV SPRING_DATASOURCE_USERNAME=hotel_admin
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/hotel_booking
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "hotel-booking.jar"]