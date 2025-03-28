package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Statistics.StatisticsDto;
import com.example.hotel_booking.model.Statistics;
import com.example.hotel_booking.service.StatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public List<StatisticsDto> getAllStatistics() {
        log.info("Получен запрос на получение всех статистик");
        return statisticsService.getAllStatistics();
    }

    @GetMapping("/type/{eventType}")
    public List<StatisticsDto> getStatisticsByType(@PathVariable String eventType) {
        log.info("Получен запрос на получение статистики по типу: {}", eventType);
        return statisticsService.getStatisticsByType(eventType);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String saveUserRegistration(@RequestParam String userId) {
        log.info("Получен запрос на регистрацию пользователя: {}", userId);
        statisticsService.saveUserRegistration(userId);
        return "User registration event sent to Kafka";
    }

    @PostMapping("/booking")
    public String saveRoomBooking(@RequestBody Statistics statistics) {
        log.info("Получен запрос на бронирование от пользователя: {}", statistics.getUserId());
        if (statistics.getDetails() == null) {
            throw new IllegalArgumentException("Booking details are required");
        }
        statisticsService.saveRoomBooking(
                statistics.getUserId(),
                statistics.getDetails().getBookingId(),
                statistics.getDetails().getRoomId(),
                statistics.getDetails().getCheckIn(),
                statistics.getDetails().getCheckOut()
        );
        log.debug("Событие бронирования пользователя {} отправлено в Kafka", statistics.getUserId());
        return "Room booking event sent to Kafka";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export/csv")
    public void exportStatisticsToCSV(HttpServletResponse response) throws IOException {
        log.info("Получен запрос на экспорт статистики в CSV");
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.csv");

        PrintWriter writer = response.getWriter();
        statisticsService.exportStatisticsToCSV(writer);
        writer.flush();
        log.debug("Файл statistics.csv успешно сгенерирован и отправлен");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
