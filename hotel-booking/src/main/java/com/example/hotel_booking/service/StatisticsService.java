package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Statistics.StatisticsDto;
import com.example.hotel_booking.dto.Statistics.StatisticsEventDto;
import com.example.hotel_booking.kafka.KafkaProducerService;
import com.example.hotel_booking.mapper.StatisticsMapper;
import com.example.hotel_booking.model.BookingDetails;
import com.example.hotel_booking.model.Statistics;
import com.example.hotel_booking.repository.StatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsService {

    private final KafkaProducerService kafkaProducerService;
    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper = StatisticsMapper.INSTANCE;

    public StatisticsService(KafkaProducerService kafkaProducerService, StatisticsRepository statisticsRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.statisticsRepository = statisticsRepository;
    }

    public void saveUserRegistration(String userId) {
        log.info("Сохранение события регистрации пользователя: {}", userId);
        StatisticsEventDto event = StatisticsEventDto.builder()
                .eventType("USER_REGISTRATION")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaProducerService.sendEvent("statistics-events", event);
    }

    public void saveRoomBooking(String userId, String bookingId, String roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        log.info("Сохранение события бронирования: userId={}, bookingId={}, roomId={}", userId, bookingId, roomId);
        StatisticsEventDto event = StatisticsEventDto.builder()
                .eventType("ROOM_BOOKING")
                .userId(userId)
                .details(new BookingDetails(bookingId, roomId, checkIn, checkOut))
                .timestamp(LocalDateTime.now())
                .build();
        kafkaProducerService.sendEvent("statistics-events", event);
    }

    public List<StatisticsDto> getAllStatistics() {
        log.info("Получение всех записей статистики");
        return statisticsRepository.findAll().stream()
                .map(statisticsMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StatisticsDto> getStatisticsByType(String eventType) {
        log.info("Получение статистики по типу: {}", eventType);
        return statisticsRepository.findByEventType(eventType).stream()
                .map(statisticsMapper::toDto)
                .collect(Collectors.toList());
    }

    public void exportStatisticsToCSV(PrintWriter writer) {
        log.info("Экспорт статистики в CSV");
        List<Statistics> statisticsList = statisticsRepository.findAll();
        writer.println("Event Type, User ID, Timestamp, Booking ID, Room ID, Check-In, Check-Out");

        for (Statistics stat : statisticsList) {
            String bookingId = (stat.getDetails() != null) ? stat.getDetails().getBookingId() : "";
            String roomId = (stat.getDetails() != null) ? stat.getDetails().getRoomId() : "";
            String checkIn = (stat.getDetails() != null && stat.getDetails().getCheckIn() != null)
                    ? stat.getDetails().getCheckIn().toString() : "";
            String checkOut = (stat.getDetails() != null && stat.getDetails().getCheckOut() != null)
                    ? stat.getDetails().getCheckOut().toString() : "";

            writer.println(String.join(",", stat.getEventType(), stat.getUserId(),
                    stat.getCreatedAt().toString(), bookingId, roomId, checkIn, checkOut));
        }
    }
}
