CREATE TABLE customer (
    dni            VARCHAR(20) NOT NULL,
    username       VARCHAR(50) NOT NULL,
    email          VARCHAR(50) NOT NULL,
    password       VARCHAR(100) NOT NULL,
    credit_card    VARCHAR(20) NOT NULL,
    total_flights  INT NOT NULL,
    total_lodgings INT NOT NULL,
    total_tours    INT NOT NULL,
    phone_number   VARCHAR(20) NOT NULL,
    enabled        BOOLEAN DEFAULT TRUE NOT NULL,
    PRIMARY KEY (dni)
);

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE customer_roles (
    customer_dni VARCHAR(20) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (customer_dni, role_id),
    CONSTRAINT fk_customer_roles_customer FOREIGN KEY (customer_dni) REFERENCES customer(dni),
    CONSTRAINT fk_customer_roles_role FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE fly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origin_lat DECIMAL NOT NULL,
    origin_lng DECIMAL NOT NULL,
    destiny_lng DECIMAL NOT NULL,
    destiny_lat DECIMAL NOT NULL,
    origin_name VARCHAR(20) NOT NULL,
    destiny_name VARCHAR(20) NOT NULL,
    aero_line VARCHAR(20) NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE hotel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(50) NOT NULL,
    rating INT NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE tour (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_customer VARCHAR(20) NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (id_customer) REFERENCES customer(dni)
);

CREATE TABLE reservation (
    id UUID PRIMARY KEY,
    date_reservation TIMESTAMP NOT NULL,
    date_start DATE NOT NULL,
    date_end DATE,
    total_days INT NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    tour_id BIGINT,
    hotel_id BIGINT,
    customer_id VARCHAR(20) NOT NULL,
    CONSTRAINT fk_customer_r FOREIGN KEY (customer_id) REFERENCES customer(dni),
    CONSTRAINT fk_hotel_r FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    CONSTRAINT fk_tour_r FOREIGN KEY (tour_id) REFERENCES tour(id) ON DELETE CASCADE
);

CREATE TABLE ticket (
    id UUID PRIMARY KEY,
    price DOUBLE PRECISION NOT NULL,
    fly_id BIGINT NOT NULL,
    customer_id VARCHAR(20) NOT NULL,
    departure_date TIMESTAMP NOT NULL,
    arrival_date TIMESTAMP NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    tour_id BIGINT,
    CONSTRAINT fk_customer_t FOREIGN KEY (customer_id) REFERENCES customer(dni),
    CONSTRAINT fk_fly_t FOREIGN KEY (fly_id) REFERENCES fly(id),
    CONSTRAINT fk_tour_t FOREIGN KEY (tour_id) REFERENCES tour(id) ON DELETE CASCADE
);
