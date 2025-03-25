package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RoomCreateRequest;
import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomUpdateRequest;
import com.example.hotel_booking.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        ResponseEntity<List<RoomDto>> response = roomController.getAllRooms();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Deluxe Room", response.getBody().get(0).getTitle());
        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void testGetRoomById() {
        RoomDto roomDto = new RoomDto(1L, 1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        when(roomService.getRoomById(1L)).thenReturn(roomDto);
        ResponseEntity<RoomDto> response = roomController.getRoomById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Deluxe Room", response.getBody().getTitle());
        verify(roomService, times(1)).getRoomById(1L);
    }

    @Test
    void testCreateRoom() {
        RoomCreateRequest request = new RoomCreateRequest(1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        RoomDto responseDto = new RoomDto(1L, 1L, "Deluxe Room", "Nice room", "101", BigDecimal.valueOf(200.0), 2);
        when(roomService.createRoom(request)).thenReturn(responseDto);
        ResponseEntity<RoomDto> response = roomController.createRoom(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Deluxe Room", response.getBody().getTitle());
        verify(roomService, times(1)).createRoom(request);
    }

    @Test
    void testUpdateRoom() {
        RoomUpdateRequest request = new RoomUpdateRequest("Updated Room", "Updated description", "202", BigDecimal.valueOf(250.0), 3);
        RoomDto responseDto = new RoomDto(1L, 1L, "Updated Room", "Updated description", "202", BigDecimal.valueOf(250.0), 3);
        when(roomService.updateRoom(1L, request)).thenReturn(responseDto);
        ResponseEntity<RoomDto> response = roomController.updateRoom(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Room", response.getBody().getTitle());
        verify(roomService, times(1)).updateRoom(1L, request);
    }

    @Test
    void testDeleteRoom() {
        doNothing().when(roomService).deleteRoom(1L);
        ResponseEntity<Void> response = roomController.deleteRoom(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roomService, times(1)).deleteRoom(1L);
    }
}
