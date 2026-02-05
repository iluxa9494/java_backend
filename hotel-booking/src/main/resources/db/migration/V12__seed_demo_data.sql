DO $$
BEGIN
  IF (SELECT COUNT(*) FROM hotels) = 0 THEN
    INSERT INTO hotels (id, name, title, city, address, distance_from_center, rating, ratings_count) VALUES
      (1, 'Aurora Skyline', 'Aurora Skyline Hotel', 'Saint Petersburg', 'Nevsky Ave 18', 0.8, 4.7, 312),
      (2, 'Baltic Pearl', 'Baltic Pearl Residence', 'Saint Petersburg', 'Liteyny Ave 42', 1.4, 4.5, 198),
      (3, 'Hermitage View', 'Hermitage View Suites', 'Saint Petersburg', 'Palace Emb 10', 0.6, 4.9, 421),
      (4, 'Moscow Meridian', 'Moscow Meridian Hotel', 'Moscow', 'Tverskaya St 7', 0.9, 4.6, 287),
      (5, 'Red Square Loft', 'Red Square Loft', 'Moscow', 'Nikolskaya St 5', 0.4, 4.8, 355),
      (6, 'Garden Ring Inn', 'Garden Ring Inn', 'Moscow', 'Sadovaya St 21', 2.1, 4.3, 142),
      (7, 'Kazan Kremlin', 'Kazan Kremlin Hotel', 'Kazan', 'Kremlevskaya St 15', 0.7, 4.6, 209),
      (8, 'Volga Breeze', 'Volga Breeze Suites', 'Kazan', 'Bauman St 30', 1.6, 4.4, 121),
      (9, 'Tatar Harmony', 'Tatar Harmony Hotel', 'Kazan', 'Universitetskaya St 12', 2.4, 4.2, 88),
      (10, 'Riverside Lane', 'Riverside Lane Hotel', 'Saint Petersburg', 'Fontanka Emb 55', 1.9, 4.1, 67);

    PERFORM setval(pg_get_serial_sequence('hotels', 'id'), (SELECT MAX(id) FROM hotels));
  END IF;

  IF (SELECT COUNT(*) FROM rooms) = 0 THEN
    INSERT INTO rooms (id, hotel_id, name, description, room_number, price, max_guests, unavailable_dates) VALUES
      (1, 1, 'Standard Double', 'Cozy room with city view.', '101', 4200.00, 2, '[]'::jsonb),
      (2, 2, 'Comfort King', 'Spacious room with balcony.', '201', 5100.00, 2, '[]'::jsonb),
      (3, 3, 'Junior Suite', 'Elegant suite near the Hermitage.', '301', 7600.00, 3, '[]'::jsonb),
      (4, 4, 'Business Twin', 'Work-ready room with desk.', '401', 5900.00, 2, '[]'::jsonb),
      (5, 5, 'Premium King', 'Premium room with square view.', '501', 8200.00, 2, '[]'::jsonb),
      (6, 6, 'Family Room', 'Family room with sofa bed.', '601', 6800.00, 4, '[]'::jsonb),
      (7, 7, 'Classic Double', 'Classic room with warm decor.', '701', 4300.00, 2, '[]'::jsonb),
      (8, 8, 'Superior Twin', 'Bright room with river view.', '801', 4700.00, 2, '[]'::jsonb),
      (9, 9, 'Economy Double', 'Simple and clean room.', '901', 3200.00, 2, '[]'::jsonb),
      (10, 10, 'Deluxe Suite', 'Large suite with lounge area.', '1001', 9000.00, 4, '[]'::jsonb);

    PERFORM setval(pg_get_serial_sequence('rooms', 'id'), (SELECT MAX(id) FROM rooms));
  END IF;
END $$;
