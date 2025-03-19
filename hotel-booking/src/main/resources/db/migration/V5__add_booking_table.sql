CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL CHECK (check_out > check_in),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_booking_dates ON bookings(check_in, check_out);
CREATE INDEX idx_booking_room ON bookings(room_id);