package com.example.hotel_booking.kafka;

import com.example.hotel_booking.dto.Statistics.StatisticsEventDto;
import com.example.hotel_booking.model.Statistics;
import com.example.hotel_booking.repository.StatisticsRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final StatisticsRepository statisticsRepository;

    public KafkaConsumerService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @KafkaListener(topics = "statistics-events", groupId = "statistics_group")
    public void consumeEvent(StatisticsEventDto event) {
        Statistics statistics = Statistics.builder()
                .eventType(event.getEventType())
                .userId(event.getUserId())
                .details(event.getDetails())
                .createdAt(event.getTimestamp())
                .build();
        statisticsRepository.save(statistics);
    }
}
