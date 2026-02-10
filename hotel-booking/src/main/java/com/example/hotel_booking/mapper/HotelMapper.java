package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Hotel.HotelCreateRequest;
import com.example.hotel_booking.dto.Hotel.HotelDto;
import com.example.hotel_booking.dto.Hotel.HotelUpdateRequest;
import com.example.hotel_booking.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

    HotelDto toDto(Hotel hotel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingsCount", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "distanceFromCenter", target = "distanceFromCenter")
    Hotel toEntity(HotelCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingsCount", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "distanceFromCenter", target = "distanceFromCenter")
    void updateHotelFromDto(HotelUpdateRequest request, @MappingTarget Hotel hotel);
}
