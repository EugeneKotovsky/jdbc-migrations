package ru.netology.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:jdbc_migrations;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@AutoConfigureMockMvc
@Sql(statements = {
        "DELETE FROM ORDERS",
        "DELETE FROM CUSTOMERS",
        "INSERT INTO CUSTOMERS(name, surname, age, phone_number) VALUES ('Alexey', 'Ivanov', 30, '+79990000000')",
        "INSERT INTO ORDERS(date, customer_id, product_name, amount) VALUES (DATE '2024-01-10', (SELECT id FROM CUSTOMERS WHERE name = 'Alexey'), 'Book', 700.00)",
        "INSERT INTO ORDERS(date, customer_id, product_name, amount) VALUES (DATE '2024-01-11', (SELECT id FROM CUSTOMERS WHERE name = 'Alexey'), 'Phone', 25000.00)"
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProductsIgnoringNameCase() throws Exception {
        mockMvc.perform(get("/products/fetch-product").param("name", "aLeXeY"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Book\",\"Phone\"]"));
    }

    @Test
    void shouldReturnEmptyListForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/products/fetch-product").param("name", "Ivan"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
