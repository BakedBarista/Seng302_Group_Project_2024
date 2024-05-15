INSERT INTO garden_user (fname, lname, email, password, dob) VALUES ('Jane', 'Doe', 'jane.doe@gmail.com', 'password', '01/01/1970');

-- Gardens
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Enchanted Oasis', '1','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1200, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Zen Harmony', '1','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 800, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Eternal Eden', '2','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1500, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Whimsical Wonderland', '3','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1000, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Secret Garden', '4','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 900, true, 1,true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Celestial Courtyard', '5','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1300, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Lush Utopia', '6','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1100, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Sapphire Retreat', '7','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 950, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Majestic Mirage', '8','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 1200, true, 1, true, false);
INSERT INTO garden (name, street_Number, street_Name, suburb, city, country, post_Code, lat, lon, size, is_public, owner_id, display_weather_alert, watering_recommendation) VALUES ('Moonlit Meadow', '9','Ilam road','Ilam','Christchurch','New Zealand','8041','0.55','0.55', 850, true, 1, true, false);

-- Garden 1
INSERT INTO plant (name, count, description, planted_date, garden_id) VALUES
                                                                                            ('Rose', 15, 'Red', '01/03/2024', 1),
                                                                                            ('Sunflower', 8, 'Yellow', '10/04/2024', 1),
                                                                                            ('Lavender', 5, 'Purple', '22/05/2024', 1),
                                                                                            ('Tulip', 12, 'Pink', '15/06/2024', 1),
                                                                                            ('Daisy', 18, 'White', '05/07/2024', 1),
                                                                                            ('Lily', 7, 'Orange', '18/08/2024', 1),
                                                                                            ('Hydrangea', 20, 'Blue', '09/09/2024', 1),
                                                                                            ('Cactus', 3, 'Green', '30/10/2024', 1),
                                                                                            ('Orchid', 10, 'Purple', '12/11/2024', 1),
                                                                                            ('Maple', 14, 'Red', '05/12/2024', 1);


