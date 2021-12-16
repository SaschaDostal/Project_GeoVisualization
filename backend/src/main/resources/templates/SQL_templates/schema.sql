CREATE TABLE PM_SENSORS (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    lon FLOAT NOT NULL,
    lat FLOAT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE DWD_STATIONS (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    lon FLOAT NOT NULL,
    lat FLOAT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE PM_VALUES (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    time_stamp VARCHAR(255) NOT NULL,
    P1 FLOAT NOT NULL,
    P2 FLOAT NOT NULL,
    PRIMARY KEY (id, time_stamp)
);