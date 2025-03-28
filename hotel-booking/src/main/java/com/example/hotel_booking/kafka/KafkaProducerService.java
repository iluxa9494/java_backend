package com.example.hotel_booking.kafka;

import com.example.hotel_booking.dto.Statistics.StatisticsEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, StatisticsEventDto> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, StatisticsEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, StatisticsEventDto event) {
        kafkaTemplate.send(topic, event);
    }
}
