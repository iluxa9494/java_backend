package com.example.hotel_booking.repository;

import com.example.hotel_booking.model.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends MongoRepository<Statistics, String> {
    List<Statistics> findByEventType(String eventType);
}
