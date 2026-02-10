package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Room.RoomCreateRequest;
import com.example.hotel_booking.dto.Room.RoomDto;
import com.example.hotel_booking.dto.Room.RoomUpdateRequest;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.jpa.HotelRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HotelRepository.class})
public interface RoomMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "hotel", ignore = true),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "roomNumber", target = "roomNumber"),
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "maxGuests", target = "maxGuests")
    })
    Room toEntity(RoomCreateRequest request);

    RoomDto toDto(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RoomUpdateRequest request, @MappingTarget Room room);
}
