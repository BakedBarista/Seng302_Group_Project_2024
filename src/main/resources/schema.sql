create table garden (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    location varchar(255) NOT NULL,
    size int NOT NULL
    );

INSERT INTO garden (name, location, size) VALUES ('Garden1', 'Location1', 100);
INSERT INTO garden (name, location, size) VALUES ('Garden2', 'Location2', 200);
INSERT INTO garden (name, location, size) VALUES ('Garden3', 'Location3', 300);
INSERT INTO garden (name, location, size) VALUES ('Garden4', 'Location4', 400);
INSERT INTO garden (name, location, size) VALUES ('Garden5', 'Location5', 500);
