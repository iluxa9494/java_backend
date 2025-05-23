package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "userId", source = "User.id")
    @Mapping(target = "hotelId", source = "hotel.id")
    RatingDto toDto(Rating rating);

    @Mapping(source = "userId", target = "User.id")
    @Mapping(source = "hotelId", target = "hotel.id")
    Rating toEntity(RatingDto ratingDto);
}
