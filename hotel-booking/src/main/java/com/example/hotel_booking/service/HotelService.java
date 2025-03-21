package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.HotelCreateRequest;
import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.dto.HotelUpdateRequest;
import com.example.hotel_booking.mapper.HotelMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
        return hotelMapper.toDto(hotel);
    }

    public HotelDto createHotel(HotelCreateRequest request) {
        Hotel hotel = hotelMapper.toEntity(request);
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toDto(hotel);
    }

    public HotelDto updateHotel(Long id, HotelUpdateRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));

        hotelMapper.updateHotelFromDto(request, hotel);
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toDto(hotel);
    }

    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }

    public Page<HotelDto> getHotels(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = (city == null || city.isBlank())
                ? hotelRepository.findAll(pageable)
                : hotelRepository.findByCityContainingIgnoreCase(city, pageable);
        return hotels.map(hotelMapper::toDto);
    }
}
