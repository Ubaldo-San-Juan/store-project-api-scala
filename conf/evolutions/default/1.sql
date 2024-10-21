CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price NUMERIC(10, 2) NOT NULL,
                          stock INT NOT NULL,
                          category_id INT NOT NULL,
                          supplier_id INT NOT NULL,
                          FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
                          FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);
