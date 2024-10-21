-- Database: UsersQrSof

-- DROP DATABASE IF EXISTS "UsersQrSof";

CREATE DATABASE "UsersQrSof"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Mexico.1252'
    LC_CTYPE = 'Spanish_Mexico.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
	
CREATE TABLE categories (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

INSERT INTO categories (name)
VALUES
  ('refrescos'),
  ('sabritas'),
  ('lacteos'),
  ('gelletas');
  
select * from categories;


CREATE TABLE suppliers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(50)
);

INSERT INTO suppliers (name, address, email, phone_number) 
VALUES ('Coca Cola', 'Pachuca, Hidalgo', 'cocacola@example.com', '123-456-7890');

INSERT INTO suppliers (name, address, email, phone_number) 
VALUES ('Sabriton', 'Zempoala, Hidalgo', 'sabriton@example.com', '234-567-8901');

INSERT INTO suppliers (name, address, email, phone_number) 
VALUES ('Barcel', 'Huejutla, Hidalgo', 'barcel@example.com', '345-678-9012');

INSERT INTO suppliers (name, address, email, phone_number) 
VALUES ('Carameladas', 'Atlapexco, Hidalgo', 'carameladas@example.com', '456-789-0123');

select * from suppliers;




CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    barcode VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL,
    category_id INT NOT NULL,
    supplier_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);



INSERT INTO products (barcode, name, description, price, stock, category_id, supplier_id) VALUES
('123912031231231321', 'Coca Cola', 'Refresco coca de 500 ml', 32.99, 50, 1, 1),
('893423231839182128', 'Tutsi Pop', 'Paleta sabor fresa', 9.99, 100, 6, 4),
('323231210993918812', 'Runners', 'Sabrita de 300 g', 15.99, 200, 2, 3),
('972837283741123219', 'Jarrito', 'Refresco sabor piña de 2 lt', 23.99, 25, 1, 1);