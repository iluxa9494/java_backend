package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Room.RoomCreateRequest;
import com.example.hotel_booking.dto.Room.RoomDto;
import com.example.hotel_booking.dto.Room.RoomUpdateRequest;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.mapper.RoomMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.jpa.HotelRepository;
import com.example.hotel_booking.repository.jpa.RoomRepository;
import com.example.hotel_booking.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    public List<RoomDto> getAllRooms() {
        log.info("Получение списка всех комнат");
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long id) {
        log.info("Получение комнаты по ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Комната с ID {} не найдена", id);
                    return new ResourceNotFoundException("Комната не найдена");
                });
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDto createRoom(RoomCreateRequest request) {
        log.info("Создание новой комнаты для отеля ID: {}", request.getHotelId());
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Отель с id " + request.getHotelId() + " не найден"));

        if (roomRepository.findByHotelIdAndRoomNumber(request.getHotelId(), request.getRoomNumber()).isPresent()) {
            throw new IllegalArgumentException("Комната с номером " + request.getRoomNumber() + " уже существует в отеле");
        }

        Room room = roomMapper.toEntity(request);
        if (room == null) {
            throw new IllegalStateException("Ошибка маппинга: roomMapper.toEntity(request) вернул null");
        }

        room.setHotel(hotel);
        room = roomRepository.save(room);
        log.info("Комната успешно создана с ID {}", room.getId());
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomUpdateRequest request) {
        log.info("Обновление комнаты с ID {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Комната с ID {} не найдена", id);
                    return new ResourceNotFoundException("Комната не найдена");
                });
        roomMapper.updateEntity(request, room);
        room = roomRepository.save(room);
        log.info("Комната с ID {} успешно обновлена", room.getId());
        return roomMapper.toDto(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        log.info("Удаление комнаты с ID {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Комната с ID {} не найдена", id);
                    return new ResourceNotFoundException("Комната не найдена");
                });
        roomRepository.delete(room);
        log.info("Комната с ID {} успешно удалена", id);
    }

    public Page<RoomDto> getFilteredRooms(Long hotelId, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Integer minGuests, Integer maxGuests,
                                          LocalDate checkIn, LocalDate checkOut, int page, int size) {
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
