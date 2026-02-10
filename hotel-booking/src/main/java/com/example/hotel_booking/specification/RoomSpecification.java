package com.example.hotel_booking.specification;

import com.example.hotel_booking.model.Room;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RoomSpecification {

    public static Specification<Room> byHotel(Long hotelId) {
        return (root, query, cb) -> (hotelId == null) ? cb.conjunction() : cb.equal(root.get("hotel").get("id"), hotelId);
    }

    public static Specification<Room> byPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Room> byGuests(Integer minGuests, Integer maxGuests) {
        return (root, query, cb) -> {
            if (minGuests == null && maxGuests == null) return cb.conjunction();
            if (minGuests == null) return cb.lessThanOrEqualTo(root.get("maxGuests"), maxGuests);
            if (maxGuests == null) return cb.greaterThanOrEqualTo(root.get("maxGuests"), minGuests);
            return cb.between(root.get("maxGuests"), minGuests, maxGuests);
        };
    }

    public static Specification<Room> isAvailableBetweenDates(LocalDate checkIn, LocalDate checkOut) {
        return (root, query, cb) -> {
            if (checkIn == null || checkOut == null) return cb.conjunction();

            Predicate checkInPredicate = cb.lessThanOrEqualTo(root.get("bookings").get("checkOut"), checkIn);
            Predicate checkOutPredicate = cb.greaterThanOrEqualTo(root.get("bookings").get("checkIn"), checkOut);

            return cb.or(checkInPredicate, checkOutPredicate);
        };
    }
}
