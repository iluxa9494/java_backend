CREATE TABLE hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    distance_from_center DECIMAL(6,2) NOT NULL,
    rating DECIMAL(2,1) NOT NULL DEFAULT 0 CHECK (rating BETWEEN 1 AND 5),
    ratings_count INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_hotel_city ON hotels(city);
CREATE INDEX idx_hotel_rating ON hotels(rating DESC);