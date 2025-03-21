package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.BookingCreateRequest;
import com.example.hotel_booking.dto.BookingDto;
import com.example.hotel_booking.dto.BookingUpdateRequest;
import com.example.hotel_booking.model.Booking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roomId", source = "room.id")
    BookingDto toDto(Booking booking);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "room", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    Booking toEntity(BookingCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BookingUpdateRequest request, @MappingTarget Booking booking);
}
