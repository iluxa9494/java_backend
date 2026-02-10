package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Room.RoomCreateRequest;
import com.example.hotel_booking.dto.Room.RoomDto;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.mapper.RoomMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRooms() {
        List<Room> rooms = List.of(new Room(1L, new Hotel(), "Luxury Suite", "Nice room", "101", 200.0, 2, null));
        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toDto(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            return new RoomDto(room.getId(), 1L, room.getTitle(), room.getDescription(), room.getNumber(), BigDecimal.valueOf(room.getPrice()), room.getMaxGuests());
        });
        List<RoomDto> result = roomService.getAllRooms();
        assertEquals(1, result.size());
        assertEquals("Luxury Suite", result.get(0).getTitle());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void testGetRoomById_WhenRoomExists() {
        Room room = new Room(1L, new Hotel(), "Luxury Suite", "Nice room", "101", 200.0, 2, null);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(room)).thenReturn(new RoomDto(room.getId(), 1L, room.getTitle(), room.getDescription(), room.getNumber(), BigDecimal.valueOf(room.getPrice()), room.getMaxGuests()));
        RoomDto result = roomService.getRoomById(1L);
        assertNotNull(result);
        assertEquals("Luxury Suite", result.getTitle());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRoomById_WhenRoomDoesNotExist() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById(1L));
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateRoom() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        RoomCreateRequest request = new RoomCreateRequest(1L, "Luxury Suite", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        Room room = new Room(1L, hotel, request.getTitle(), request.getDescription(), request.getNumber(), request.getPrice().doubleValue(), request.getMaxGuests(), null);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomMapper.toEntity(request)).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomDto(room.getId(), 1L, room.getTitle(), room.getDescription(), room.getNumber(), BigDecimal.valueOf(room.getPrice()), room.getMaxGuests()));
        RoomDto result = roomService.createRoom(request);
        assertNotNull(result);
        assertEquals("Luxury Suite", result.getTitle());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testDeleteRoom() {
        Room room = new Room(1L, new Hotel(), "Luxury Suite", "Nice room", "101", 200.0, 2, null);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        roomService.deleteRoom(1L);
        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    void testIsRoomAvailable() {
        Room room = new Room(1L, new Hotel(), "Luxury Suite", "Nice room", "101", 200.0, 2, List.of());
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        boolean isAvailable = roomService.isRoomAvailable(1L, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        assertTrue(isAvailable);
        verify(roomRepository, times(1)).findById(1L);
    }
}
