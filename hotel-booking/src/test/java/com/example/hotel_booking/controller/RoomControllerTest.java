package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Room.RoomCreateRequest;
import com.example.hotel_booking.dto.Room.RoomDto;
import com.example.hotel_booking.dto.Room.RoomUpdateRequest;
import com.example.hotel_booking.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRooms() {
        List<RoomDto> rooms = List.of(new RoomDto(1L, 1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2));
        when(roomService.getAllRooms()).thenReturn(rooms);

        List<RoomDto> result = roomController.getAllRooms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Deluxe Room", result.get(0).getName());
        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void testGetRoomById() {
        RoomDto roomDto = new RoomDto(1L, 1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        when(roomService.getRoomById(1L)).thenReturn(roomDto);

        RoomDto result = roomController.getRoomById(1L);

        assertNotNull(result);
        assertEquals("Deluxe Room", result.getName());
        verify(roomService, times(1)).getRoomById(1L);
    }

    @Test
    void testCreateRoom() {
        RoomCreateRequest request = new RoomCreateRequest(1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        RoomDto responseDto = new RoomDto(1L, 1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        when(roomService.createRoom(request)).thenReturn(responseDto);

        RoomDto result = roomController.createRoom(request);

        assertNotNull(result);
        assertEquals("Deluxe Room", result.getName());
        verify(roomService, times(1)).createRoom(request);
    }

    @Test
    void testUpdateRoom() {
        RoomUpdateRequest request = new RoomUpdateRequest("Updated Room", "Updated description", "202", BigDecimal.valueOf(250.0), 3);
        RoomDto responseDto = new RoomDto(1L, 1L, "Updated Room", "Updated description", "202", BigDecimal.valueOf(250.0), 3);
        when(roomService.updateRoom(1L, request)).thenReturn(responseDto);

        RoomDto result = roomController.updateRoom(1L, request);

        assertNotNull(result);
        assertEquals("Updated Room", result.getName());
        verify(roomService, times(1)).updateRoom(1L, request);
    }

    @Test
    void testDeleteRoom() {
        doNothing().when(roomService).deleteRoom(1L);
        roomController.deleteRoom(1L);
        verify(roomService, times(1)).deleteRoom(1L);
    }
}
