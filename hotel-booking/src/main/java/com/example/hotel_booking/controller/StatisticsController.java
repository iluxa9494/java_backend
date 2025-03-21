package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.StatisticsDto;
import com.example.hotel_booking.model.Statistics;
import com.example.hotel_booking.service.StatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<List<StatisticsDto>> getAllStatistics() {
        return ResponseEntity.ok(statisticsService.getAllStatistics());
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<StatisticsDto>> getStatisticsByType(@PathVariable String eventType) {
        return ResponseEntity.ok(statisticsService.getStatisticsByType(eventType));
    }

    @PostMapping("/register")
    public ResponseEntity<String> saveUserRegistration(@RequestParam String userId) {
        statisticsService.saveUserRegistration(userId);
        return ResponseEntity.ok("User registration event sent to Kafka");
    }

    @PostMapping("/booking")
    public ResponseEntity<String> saveRoomBooking(@RequestBody Statistics statistics) {
        if (statistics.getDetails() == null) {
            return ResponseEntity.badRequest().body("Booking details are required");
        }
        statisticsService.saveRoomBooking(
                statistics.getUserId(),
                statistics.getDetails().getBookingId(),
                statistics.getDetails().getRoomId(),
                statistics.getDetails().getCheckIn(),
                statistics.getDetails().getCheckOut()
        );
        return ResponseEntity.ok("Room booking event sent to Kafka");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export/csv")
    public void exportStatisticsToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.csv");

        PrintWriter writer = response.getWriter();
        statisticsService.exportStatisticsToCSV(writer);
        writer.flush();
    }
}