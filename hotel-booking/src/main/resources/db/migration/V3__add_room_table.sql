CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    room_number VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    max_guests INT NOT NULL CHECK (max_guests > 0),
    unavailable_dates JSONB DEFAULT '[]'::jsonb,
    UNIQUE (hotel_id, room_number)
);

CREATE INDEX idx_room_price ON rooms(price);
CREATE INDEX idx_room_hotel_id ON rooms(hotel_id);