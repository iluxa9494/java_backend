package com.example.hotel_booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.Socket;

@Slf4j
@SpringBootApplication
public class HotelBookingApplication {

	public static void main(String[] args) {
		waitForService("localhost", 9092, "Kafka");
		waitForService("localhost", 27017, "MongoDB");

		SpringApplication.run(HotelBookingApplication.class, args);
		log.info("Hotel Booking Application успешно запущено!");
	}

	private static void waitForService(String host, int port, String serviceName) {
		int attempts = 0;
		while (attempts < 10) {
			try (Socket socket = new Socket(host, port)) {
				log.info("✅ {} доступен, продолжаем запуск...", serviceName);
				return;
			} catch (Exception e) {
				attempts++;
				log.warn("⏳ {} еще не доступен. Повторная попытка через 5 секунд... (Попытка {}/{})", serviceName, attempts, 10);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ignored) {
				}
			}
		}
		log.error("❌ {} не доступен после 10 попыток. Остановка запуска!", serviceName);
		System.exit(1);
	}
}
