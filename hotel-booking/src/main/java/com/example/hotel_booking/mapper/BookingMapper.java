package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Booking.BookingCreateRequest;
import com.example.hotel_booking.dto.Booking.BookingDto;
import com.example.hotel_booking.dto.Booking.BookingUpdateRequest;
import com.example.hotel_booking.model.Booking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userId", source = "User.id")
    @Mapping(target = "roomId", source = "room.id")
    BookingDto toDto(Booking booking);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "User", ignore = true),
            @Mapping(target = "room", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    Booking toEntity(BookingCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BookingUpdateRequest request, @MappingTarget Booking booking);
}
