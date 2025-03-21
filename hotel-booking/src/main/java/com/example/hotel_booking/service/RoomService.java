package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RoomCreateRequest;
import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomUpdateRequest;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.mapper.RoomMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.repository.RoomRepository;
import com.example.hotel_booking.specification.RoomSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;


    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комната с id " + id + " не найдена"));
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDto createRoom(RoomCreateRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Отель с id " + request.getHotelId() + " не найден"));

        if (roomRepository.findByHotelIdAndNumber(request.getHotelId(), request.getNumber()).isPresent()) {
            throw new IllegalArgumentException("Комната с номером " + request.getNumber() + " уже существует в отеле");
        }

        Room room = roomMapper.toEntity(request);
        if (room == null) {
            throw new IllegalStateException("Ошибка маппинга: roomMapper.toEntity(request) вернул null");
        }

        room.setHotel(hotel);
        room = roomRepository.save(room);
        return roomMapper.toDto(room);
    }


    @Transactional
    public RoomDto updateRoom(Long id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комната с id " + id + " не найдена"));

        roomMapper.updateEntity(request, room);
        room = roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Комната с id " + id + " не найдена"));
        roomRepository.delete(room);
    }

    public Page<RoomDto> getFilteredRooms(Long hotelId, Double minPrice, Double maxPrice, Integer minGuests, Integer maxGuests, LocalDate checkIn, LocalDate checkOut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Room> spec = Specification.where(RoomSpecification.byHotel(hotelId))
                .and(RoomSpecification.byPrice(minPrice, maxPrice))
                .and(RoomSpecification.byGuests(minGuests, maxGuests))
                .and(RoomSpecification.isAvailableBetweenDates(checkIn, checkOut));

        Page<Room> rooms = roomRepository.findAll(spec, pageable);
        return rooms.map(roomMapper::toDto);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findById(roomId)
                .map(room -> room.getBookings().stream()
                        .noneMatch(booking -> !(booking.getCheckOut().isBefore(checkIn) || booking.getCheckIn().isAfter(checkOut))))
                .orElse(false);
    }
}