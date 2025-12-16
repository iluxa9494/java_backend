package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "hotelId", source = "hotel.id")
    RatingDto toDto(Rating rating);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "hotelId", target = "hotel.id")
    Rating toEntity(RatingDto ratingDto);
}