/*
 * This project is licensed under the MIT license. Module model-view-viewmodel is using ZK framework licensed under LGPL (see lgpl-3.0.txt).
 *
 * The MIT License
 * Copyright © 2014-2022 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.serialized.lob;

import org.h2.jdbcx.JdbcDataSource;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the customer class works correctly
 */
class CustomerTest {
    private static final String DB_URL = "jdbc:h2:~/test";

    private static DataSource createDataSource() {
        var dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        return dataSource;
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Connect to database
        try (var connection = DriverManager.getConnection(DB_URL);
             var statement = connection.createStatement()) {
            // Create schema
            statement.execute(Customer.DELETE_SCHEMA_SQL);
            statement.execute(Customer.CREATE_SCHEMA_SQL);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var connection = DriverManager.getConnection(DB_URL);
             var statement = connection.createStatement()) {
            statement.execute(Customer.DELETE_SCHEMA_SQL);
        }
    }

    @Test
    void testAddDepartment() {
        var customer = new Customer("customer", 1);
        var department1 = new Department("department1");
        var department2 = new Department("department2");
        customer.getDepartments().add(department1);
        customer.getDepartments().add(department2);
        assertEquals(2, customer.getDepartments().size());
    }

    @Test
    void testGetDepartments() {
        var customer = new Customer("customer", 1);
        assertNotNull(customer.getDepartments());
    }

    @Test
    void testStringToElement() throws IOException, JDOMException {
        var str = "<departmentList><department name=\"department1\"><department" +
                " name=\"department2\"><department name=\"department3\"" +
                " /></department></department></departmentList>";
        assertNotNull(Customer.stringToElement(str));
    }

    @Test
    void testElementToString() {
        var customer = new Customer("customer", 1);
        assertNotNull(Customer.elementToString(customer.departmentsToXMLElement()));
    }

    @Test
    void testInsert() throws SQLException {
        var customer = new Customer("customer", 1);
        var dataSource = createDataSource();
        assertNotEquals(0, customer.insert(dataSource));
    }

    @Test
    void testLoad() throws IOException, JDOMException, SQLException {
        var customer = new Customer("customer", 1);
        var dataSource = createDataSource();
        customer.insert(dataSource);
        assertNotNull(customer.load(1, dataSource));
    }

    @Test
    void testDepartmentsToXmlElement() {
        var customer = new Customer("customer", 1);
        assertNotNull(customer.departmentsToXMLElement());
    }

    @Test
    void testReadDepartments() {
        var customer = new Customer("customer", 1);
        var department = new Department("department");
        customer.getDepartments().add(department);
        customer.readDepartments(customer.departmentsToXMLElement());
        assertNotEquals(0, customer.getDepartments().size());
    }
}