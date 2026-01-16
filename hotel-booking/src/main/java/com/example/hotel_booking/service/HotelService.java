package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Hotel.HotelCreateRequest;
import com.example.hotel_booking.dto.Hotel.HotelDto;
import com.example.hotel_booking.dto.Hotel.HotelUpdateRequest;
import com.example.hotel_booking.mapper.HotelMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.repository.jpa.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper = HotelMapper.INSTANCE;

    public List<HotelDto> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    public HotelDto getHotelById(Long id) {
        log.info("Получение отеля по ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Отель с ID {} не найден", id);
                    return new EntityNotFoundException("Отель не найден");
                });
        return hotelMapper.toDto(hotel);
    }

    public HotelDto createHotel(HotelCreateRequest request) {
        log.info("Добавление нового отеля: {}", request.getName());
        Hotel hotel = hotelMapper.toEntity(request);
        hotel = hotelRepository.save(hotel);
        log.info("Отель '{}' успешно добавлен с ID {}", hotel.getName(), hotel.getId());
        return hotelMapper.toDto(hotel);
    }

    public HotelDto updateHotel(Long id, HotelUpdateRequest request) {
        log.info("Обновление отеля с ID {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Отель с ID {} не найден", id);
                    return new EntityNotFoundException("Отель не найден");
                });
        hotelMapper.updateHotelFromDto(request, hotel);
        hotel = hotelRepository.save(hotel);
        log.info("Отель с ID {} успешно обновлён", hotel.getId());
        return hotelMapper.toDto(hotel);
    }

    public void deleteHotel(Long id) {
        log.info("Удаление отеля с ID {}", id);
        if (!hotelRepository.existsById(id)) {
            log.warn("Попытка удалить несуществующий отель с ID {}", id);
            throw new EntityNotFoundException("Отель не найден");
        }
        hotelRepository.deleteById(id);
        log.info("Отель с ID {} успешно удалён", id);
    }

    public Page<HotelDto> getHotels(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = (city == null || city.isBlank())
                ? hotelRepository.findAll(pageable)
                : hotelRepository.findByCityContainingIgnoreCase(city, pageable);
        return hotels.map(hotelMapper::toDto);
    }
}
