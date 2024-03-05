-- Gardens
INSERT INTO garden (name, location, size) VALUES ('Enchanted Oasis', 'Mystic Meadow', 1200);
INSERT INTO garden (name, location, size) VALUES ('Zen Harmony', 'Tranquil Terrace', 800);
INSERT INTO garden (name, location, size) VALUES ('Eternal Eden', 'Serene Sanctuary', 1500);
INSERT INTO garden (name, location, size) VALUES ('Whimsical Wonderland', 'Dreamy Dell', 1000);
INSERT INTO garden (name, location, size) VALUES ('Secret Garden', 'Hidden Haven', 900);
INSERT INTO garden (name, location, size) VALUES ('Celestial Courtyard', 'Stellar Space', 1300);
INSERT INTO garden (name, location, size) VALUES ('Lush Utopia', 'Verdant Vale', 1100);
INSERT INTO garden (name, location, size) VALUES ('Sapphire Retreat', 'Azure Arbor', 950);
INSERT INTO garden (name, location, size) VALUES ('Majestic Mirage', 'Ephemeral Enclave', 1200);
INSERT INTO garden (name, location, size) VALUES ('Moonlit Meadow', 'Silver Shores', 850);

-- Garden 1
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Rose', 15, 'Red', '2024-03-01', 1),
                                                                          ('Sunflower', 8, 'Yellow', '2024-04-10', 1),
                                                                          ('Lavender', 5, 'Purple', '2024-05-22', 1),
                                                                          ('Tulip', 12, 'Pink', '2024-06-15', 1),
                                                                          ('Daisy', 18, 'White', '2024-07-05', 1),
                                                                          ('Lily', 7, 'Orange', '2024-08-18', 1),
                                                                          ('Hydrangea', 20, 'Blue', '2024-09-09', 1),
                                                                          ('Cactus', 3, 'Green', '2024-10-30', 1),
                                                                          ('Orchid', 10, 'Purple', '2024-11-12', 1),
                                                                          ('Maple', 14, 'Red', '2024-12-05', 1);


-- Garden 2
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Fern', 10, 'Green', '2024-03-05', 2),
                                                                          ('Tiger Lily', 15, 'Orange', '2024-04-18', 2),
                                                                          ('Bamboo', 6, 'Green', '2024-05-30', 2),
                                                                          ('Chrysanthemum', 9, 'Yellow', '2024-06-22', 2),
                                                                          ('Poinsettia', 12, 'Red', '2024-07-15', 2),
                                                                          ('Carnation', 8, 'Pink', '2024-08-28', 2),
                                                                          ('Daffodil', 11, 'Yellow', '2024-09-19', 2),
                                                                          ('Aloe Vera', 5, 'Green', '2024-10-10', 2),
                                                                          ('Hibiscus', 14, 'Red', '2024-11-30', 2),
                                                                          ('Iris', 7, 'Purple', '2024-12-23', 2);

-- Garden 3
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Cherry Blossom', 14, 'Pink', '2024-03-05', 3),
                                                                          ('Bird of Paradise', 10, 'Orange', '2024-04-18', 3),
                                                                          ('Bleeding Heart', 6, 'Red', '2024-05-30', 3),
                                                                          ('Columbine', 9, 'Blue', '2024-06-22', 3),
                                                                          ('Daffodil', 12, 'Yellow', '2024-07-15', 3),
                                                                          ('Eucalyptus', 8, 'Green', '2024-08-28', 3),
                                                                          ('Forsythia', 11, 'Yellow', '2024-09-19', 3),
                                                                          ('Goldenrod', 5, 'Yellow', '2024-10-10', 3),
                                                                          ('Heather', 14, 'Purple', '2024-11-30', 3),
                                                                          ('Ivy', 7, 'Green', '2024-12-23', 3);

-- Garden 4
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Dahlia', 14, 'Pink', '2024-03-05', 4),
                                                                          ('Fuchsia', 10, 'Purple', '2024-04-18', 4),
                                                                          ('Bougainvillea', 6, 'Red', '2024-05-30', 4),
                                                                          ('Zinnia', 9, 'Yellow', '2024-06-22', 4),
                                                                          ('Marigold', 12, 'Orange', '2024-07-15', 4),
                                                                          ('Hollyhock', 8, 'White', '2024-08-28', 4),
                                                                          ('Morning Glory', 11, 'Blue', '2024-09-19', 4),
                                                                          ('Pansy', 5, 'Purple', '2024-10-10', 4),
                                                                          ('Petunia', 14, 'Pink', '2024-11-30', 4),
                                                                          ('Aster', 7, 'Purple', '2024-12-23', 4);

-- Garden 5
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Geranium', 14, 'Red', '2024-03-05', 5),
                                                                          ('Hibiscus', 10, 'Pink', '2024-04-18', 5),
                                                                          ('Iris', 6, 'Purple', '2024-05-30', 5),
                                                                          ('Jasmine', 9, 'White', '2024-06-22', 5),
                                                                          ('Kangaroo Paw', 12, 'Yellow', '2024-07-15', 5),
                                                                          ('Lantana', 8, 'Orange', '2024-08-28', 5),
                                                                          ('Magnolia', 11, 'White', '2024-09-19', 5),
                                                                          ('Narcissus', 5, 'Yellow', '2024-10-10', 5),
                                                                          ('Oleander', 14, 'Pink', '2024-11-30', 5),
                                                                          ('Peony', 7, 'Red', '2024-12-23', 5);

-- Garden 6
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Quince', 9, 'Orange', '2024-03-01', 6),
                                                                          ('Ranunculus', 18, 'Red', '2024-04-10', 6),
                                                                          ('Sage', 11, 'Purple', '2024-05-22', 6),
                                                                          ('Thistle', 5, 'Purple', '2024-06-15', 6),
                                                                          ('Umbrella Grass', 13, 'Green', '2024-07-05', 6),
                                                                          ('Verbena', 15, 'Purple', '2024-08-18', 6),
                                                                          ('Wisteria', 8, 'Purple', '2024-09-09', 6),
                                                                          ('Xerophyte', 10, 'Green', '2024-10-30', 6),
                                                                          ('Yarrow', 16, 'Yellow', '2024-11-12', 6),
                                                                          ('Zebra Plant', 12, 'Green', '2024-12-05', 6);

-- Garden 7
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Jasmine', 9, 'White', '2024-03-05', 7),
                                                                          ('Kangaroo Paw', 18, 'Orange', '2024-04-10', 7),
                                                                          ('Lily of the Valley', 11, 'White', '2024-05-22', 7),
                                                                          ('Maranta', 5, 'Green', '2024-06-15', 7),
                                                                          ('Nasturtium', 13, 'Orange', '2024-07-05', 7),
                                                                          ('Oregano', 15, 'Green', '2024-08-18', 7),
                                                                          ('Palm Tree', 8, 'Green', '2024-09-09', 7),
                                                                          ('Queen Annes Lace', 10, 'White', '2024-10-30', 7),
                                                                          ('Rosemary', 16, 'Blue', '2024-11-12', 7),
                                                                          ('Sedum', 12, 'Pink', '2024-12-05', 7);

-- Garden 8
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Tansy', 9, 'Yellow', '2024-03-01', 8),
                                                                          ('Uva Ursi', 18, 'Green', '2024-04-10', 8),
                                                                          ('Vinca', 11, 'Purple', '2024-05-22', 8),
                                                                          ('Wallflower', 5, 'Orange', '2024-06-15', 8),
                                                                          ('Xanadu Philodendron', 13, 'Green', '2024-07-05', 8),
                                                                          ('Yucca', 15, 'White', '2024-08-18', 8),
                                                                          ('Zantedeschia', 8, 'Pink', '2024-09-09', 8),
                                                                          ('Aubrieta', 10, 'Purple', '2024-10-30', 8),
                                                                          ('Baptisia', 16, 'Blue', '2024-11-12', 8),
                                                                          ('Celosia', 12, 'Red', '2024-12-05', 8);

-- Garden 9
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Dahlia', 14, 'Pink', '2024-03-05', 9),
                                                                          ('Echinops', 10, 'Blue', '2024-04-18', 9),
                                                                          ('Fernleaf Yarrow', 6, 'Yellow', '2024-05-30', 9),
                                                                          ('Gardenia', 9, 'White', '2024-06-22', 9),
                                                                          ('Hellebore', 12, 'Purple', '2024-07-15', 9),
                                                                          ('Iceland Poppy', 8, 'Orange', '2024-08-28', 9),
                                                                          ('Jacaranda', 11, 'Purple', '2024-09-19', 9),
                                                                          ('Kalmia', 5, 'Pink', '2024-10-10', 9),
                                                                          ('Liatris', 14, 'Purple', '2024-11-30', 9),
                                                                          ('Monarda', 7, 'Red', '2024-12-23', 9);

-- Garden 10
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                          ('Lobelia', 9, 'Blue', '2024-03-01', 10),
                                                                          ('Daisy', 18, 'White', '2024-04-10', 10),
                                                                          ('Begonia', 11, 'Red', '2024-05-22', 10),
                                                                          ('Chamomile', 5, 'White', '2024-06-15', 10),
                                                                          ('Crocus', 13, 'Purple', '2024-07-05', 10),
                                                                          ('Aconitum', 15, 'Blue', '2024-08-18', 10),
                                                                          ('Camellia', 8, 'Pink', '2024-09-09', 10),
                                                                          ('Dianthus', 10, 'Red', '2024-10-30', 10),
                                                                          ('Echinacea', 16, 'Purple', '2024-11-12', 10),
                                                                          ('Freesia', 12, 'Yellow', '2024-12-05', 10);
