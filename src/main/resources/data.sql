-- Gardens
INSERT INTO garden (name, location, size, is_Public) VALUES ('Enchanted Oasis', 'Mystic Meadow', 1200, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Zen Harmony', 'Tranquil Terrace', 800, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Eternal Eden', 'Serene Sanctuary', 1500, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Whimsical Wonderland', 'Dreamy Dell', 1000, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Secret Garden', 'Hidden Haven', 900, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Celestial Courtyard', 'Stellar Space', 1300, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Lush Utopia', 'Verdant Vale', 1100, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Sapphire Retreat', 'Azure Arbor', 950, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Majestic Mirage', 'Ephemeral Enclave', 1200, false);
INSERT INTO garden (name, location, size, is_Public) VALUES ('Moonlit Meadow', 'Silver Shores', 850, false);

-- Garden 1
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Rose', 15, 'Red', '01/03/2024', 1, '/Plant1.jpg'),
                                                                          ('Sunflower', 8, 'Yellow', '10/04/2024', 1, '/Plant2.jpg'),
                                                                          ('Lavender', 5, 'Purple', '22/05/2024', 1, '/Plant3.jpg'),
                                                                          ('Tulip', 12, 'Pink', '15/06/2024', 1, '/Plant4.jpg'),
                                                                          ('Daisy', 18, 'White', '05/07/2024', 1, '/Plant5.jpg'),
                                                                          ('Lily', 7, 'Orange', '18/08/2024', 1, '/Plant6.jpg'),
                                                                          ('Hydrangea', 20, 'Blue', '09/09/2024', 1, '/Plant7.jpg'),
                                                                          ('Cactus', 3, 'Green', '30/10/2024', 1, '/Plant8.jpg'),
                                                                          ('Orchid', 10, 'Purple', '12/11/2024', 1, '/Plant9.jpg'),
                                                                          ('Maple', 14, 'Red', '05/12/2024', 1, '/Plant10.jpg');


-- Garden 2
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Fern', 10, 'Green', '05/03/2024', 2, '/Plant1.jpg'),
                                                                          ('Tiger Lily', 15, 'Orange', '18/04/2024', 2, '/Plant2.jpg'),
                                                                          ('Bamboo', 6, 'Green', '30/05/2024', 2, '/Plant3.jpg'),
                                                                          ('Chrysanthemum', 9, 'Yellow', '22/06/2024', 2, '/Plant4.jpg'),
                                                                          ('Poinsettia', 12, 'Red', '15/07/2024', 2, '/Plant5.jpg'),
                                                                          ('Carnation', 8, 'Pink', '28/08/2024', 2, '/Plant6.jpg'),
                                                                          ('Daffodil', 11, 'Yellow', '19/09/2024', 2, '/Plant7.jpg'),
                                                                          ('Aloe Vera', 5, 'Green', '10/10/2024', 2, '/Plant8.jpg'),
                                                                          ('Hibiscus', 14, 'Red', '30/11/2024', 2, '/Plant9.jpg'),
                                                                          ('Iris', 7, 'Purple', '23/12/2024', 2, '/Plant10.jpg');


-- Garden 3
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Cherry Blossom', 14, 'Pink', '05/03/2024', 3, '/Plant1.jpg'),
                                                                          ('Bird of Paradise', 10, 'Orange', '18/04/2024', 3, '/Plant2.jpg'),
                                                                          ('Bleeding Heart', 6, 'Red', '30/05/2024', 3, '/Plant3.jpg'),
                                                                          ('Columbine', 9, 'Blue', '22/06/2024', 3, '/Plant4.jpg'),
                                                                          ('Daffodil', 12, 'Yellow', '15/07/2024', 3, '/Plant5.jpg'),
                                                                          ('Eucalyptus', 8, 'Green', '28/08/2024', 3, '/Plant6.jpg'),
                                                                          ('Forsythia', 11, 'Yellow', '19/09/2024', 3, '/Plant7.jpg'),
                                                                          ('Goldenrod', 5, 'Yellow', '10/10/2024', 3, '/Plant8.jpg'),
                                                                          ('Heather', 14, 'Purple', '30/11/2024', 3, '/Plant9.jpg'),
                                                                          ('Ivy', 7, 'Green', '23/12/2024', 3, '/Plant10.jpg');

-- Garden 4
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Dahlia', 14, 'Pink', '05/03/2024', 4, '/Plant1.jpg'),
                                                                          ('Fuchsia', 10, 'Purple', '18/04/2024', 4, '/Plant2.jpg'),
                                                                          ('Gardenia', 6, 'White', '30/05/2024', 4, '/Plant3.jpg'),
                                                                          ('Hibiscus', 9, 'Red', '22/06/2024', 4, '/Plant4.jpg'),
                                                                          ('Impatiens', 12, 'Pink', '15/07/2024', 4, '/Plant5.jpg'),
                                                                          ('Jasmine', 8, 'White', '28/08/2024', 4, '/Plant6.jpg'),
                                                                          ('Kangaroo Paw', 11, 'Yellow', '19/09/2024', 4, '/Plant7.jpg'),
                                                                          ('Lantana', 5, 'Orange', '10/10/2024', 4, '/Plant8.jpg'),
                                                                          ('Magnolia', 14, 'White', '30/11/2024', 4, '/Plant9.jpg'),
                                                                          ('Narcissus', 7, 'Yellow', '23/12/2024', 4, '/Plant10.jpg');


-- Garden 5
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Geranium', 14, 'Red', '05/03/2024', 5, '/Plant1.jpg'),
                                                                          ('Hibiscus', 10, 'Pink', '18/04/2024', 5, '/Plant2.jpg'),
                                                                          ('Iris', 6, 'Purple', '30/05/2024', 5, '/Plant3.jpg'),
                                                                          ('Jasmine', 9, 'White', '22/06/2024', 5, '/Plant4.jpg'),
                                                                          ('Kangaroo Paw', 12, 'Yellow', '15/07/2024', 5, '/Plant5.jpg'),
                                                                          ('Lantana', 8, 'Orange', '28/08/2024', 5, '/Plant6.jpg'),
                                                                          ('Magnolia', 11, 'White', '19/09/2024', 5, '/Plant7.jpg'),
                                                                          ('Narcissus', 5, 'Yellow', '10/10/2024', 5, '/Plant8.jpg'),
                                                                          ('Oleander', 14, 'Pink', '30/11/2024', 5, '/Plant9.jpg'),
                                                                          ('Peony', 7, 'Red', '23/12/2024', 5, '/Plant10.jpg');


-- Garden 6
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Quince', 9, 'Orange', '01/03/2024', 6, '/Plant1.jpg'),
                                                                          ('Ranunculus', 18, 'Red', '10/04/2024', 6, '/Plant2.jpg'),
                                                                          ('Sage', 11, 'Purple', '22/05/2024', 6, '/Plant3.jpg'),
                                                                          ('Thistle', 5, 'Purple', '15/06/2024', 6, '/Plant4.jpg'),
                                                                          ('Umbrella Grass', 13, 'Green', '05/07/2024', 6, '/Plant5.jpg'),
                                                                          ('Verbena', 15, 'Purple', '18/08/2024', 6, '/Plant6.jpg'),
                                                                          ('Wisteria', 8, 'Purple', '09/09/2024', 6, '/Plant7.jpg'),
                                                                          ('Xerophyte', 10, 'Green', '30/10/2024', 6, '/Plant8.jpg'),
                                                                          ('Yarrow', 16, 'Yellow', '12/11/2024', 6, '/Plant9.jpg'),
                                                                          ('Zebra Plant', 12, 'Green', '05/12/2024', 6, '/Plant10.jpg');


-- Garden 7
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Jasmine', 9, 'White', '05/03/2024', 7, '/Plant1.jpg'),
                                                                          ('Kangaroo Paw', 18, 'Orange', '10/04/2024', 7, '/Plant2.jpg'),
                                                                          ('Lily of the Valley', 11, 'White', '22/05/2024', 7, '/Plant3.jpg'),
                                                                          ('Maranta', 5, 'Green', '15/06/2024', 7, '/Plant4.jpg'),
                                                                          ('Nasturtium', 13, 'Orange', '05/07/2024', 7, '/Plant5.jpg'),
                                                                          ('Oregano', 15, 'Green', '18/08/2024', 7, '/Plant6.jpg'),
                                                                          ('Palm Tree', 8, 'Green', '09/09/2024', 7, '/Plant7.jpg'),
                                                                          ('Queen Annes Lace', 10, 'White', '30/10/2024', 7, '/Plant8.jpg'),
                                                                          ('Rosemary', 16, 'Blue', '12/11/2024', 7, '/Plant9.jpg'),
                                                                          ('Sedum', 12, 'Pink', '05/12/2024', 7, '/Plant10.jpg');


-- Garden 8
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Tansy', 9, 'Yellow', '01/03/2024', 8, '/Plant1.jpg'),
                                                                          ('Uva Ursi', 18, 'Green', '10/04/2024', 8, '/Plant2.jpg'),
                                                                          ('Vinca', 11, 'Purple', '22/05/2024', 8, '/Plant3.jpg'),
                                                                          ('Wallflower', 5, 'Orange', '15/06/2024', 8, '/Plant4.jpg'),
                                                                          ('Xanadu Philodendron', 13, 'Green', '05/07/2024', 8, '/Plant5.jpg'),
                                                                          ('Yucca', 15, 'White', '18/08/2024', 8, '/Plant6.jpg'),
                                                                          ('Zantedeschia', 8, 'Pink', '09/09/2024', 8, '/Plant7.jpg'),
                                                                          ('Aubrieta', 10, 'Purple', '30/10/2024', 8, '/Plant8.jpg'),
                                                                          ('Baptisia', 16, 'Blue', '12/11/2024', 8, '/Plant9.jpg'),
                                                                          ('Celosia', 12, 'Red', '05/12/2024', 8, '/Plant10.jpg');


-- Garden 9
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Dahlia', 14, 'Pink', '05/03/2024', 9, '/Plant1.jpg'),
                                                                          ('Echinops', 10, 'Blue', '18/04/2024', 9, '/Plant2.jpg'),
                                                                          ('Fernleaf Yarrow', 6, 'Yellow', '30/05/2024', 9, '/Plant3.jpg'),
                                                                          ('Gardenia', 9, 'White', '22/06/2024', 9, '/Plant4.jpg'),
                                                                          ('Hellebore', 12, 'Purple', '15/07/2024', 9, '/Plant5.jpg'),
                                                                          ('Iceland Poppy', 8, 'Orange', '28/08/2024', 9, '/Plant6.jpg'),
                                                                          ('Jacaranda', 11, 'Purple', '19/09/2024', 9, '/Plant7.jpg'),
                                                                          ('Kalmia', 5, 'Pink', '10/10/2024', 9, '/Plant8.jpg'),
                                                                          ('Liatris', 14, 'Purple', '30/11/2024', 9, '/Plant9.jpg'),
                                                                          ('Monarda', 7, 'Red', '23/12/2024', 9, '/Plant10.jpg');


-- Garden 10
INSERT INTO plant (name, count, description, planted_date, garden_id, plant_image_path) VALUES
                                                                          ('Lobelia', 9, 'Blue', '01/03/2024', 10, '/Plant1.jpg'),
                                                                          ('Mimosa', 18, 'Pink', '10/04/2024', 10, '/Plant2.jpg'),
                                                                          ('Narcissus', 11, 'White', '22/05/2024', 10, '/Plant3.jpg'),
                                                                          ('Oleander', 5, 'Red', '15/06/2024', 10, '/Plant4.jpg'),
                                                                          ('Pansy', 13, 'Purple', '05/07/2024', 10, '/Plant5.jpg'),
                                                                          ('Quince', 15, 'Orange', '18/08/2024', 10, '/Plant6.jpg'),
                                                                          ('Ranunculus', 8, 'Red', '09/09/2024', 10, '/Plant7.jpg'),
                                                                          ('Sage', 10, 'Purple', '30/10/2024', 10, '/Plant8.jpg'),
                                                                          ('Thistle', 16, 'Purple', '12/11/2024', 10, '/Plant9.jpg'),
                                                                          ('Umbrella Grass', 12, 'Green', '05/12/2024', 10, '/Plant10.jpg');


