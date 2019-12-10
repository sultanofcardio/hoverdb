package com.sultanofcardio.database;

import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.ResourceSet;
import com.sultanofcardio.database.sql.statement.Select;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sultanofcardio.database.sql.types.Types.MySQL;
import static com.sultanofcardio.database.sql.types.Types.PostgreSQL;
import static org.junit.Assert.*;

public class PostgreSQLTest {

    @BeforeClass
    public static void setup() {
        Database.connect("db", PostgreSQL, "localhost",
                "3307", "user", "", "postgresqldb");
    }

    @AfterClass
    public static void tearDown(){
        
    }

    @Test
    public void postgreSQLSelectTest() throws SQLException {
        Database database = Database.getInstance("postgresqldb");

        database.select()
                .from("test_table")
                .execute((resultSet -> {
                    assertNotNull(resultSet);

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String words = resultSet.getString("words");
                        System.out.println(String.format("Row{id=%s, words=%s}", id, words));
                    }
                }));
    }

    @Test
    public void postgreSQLInsertTest() throws SQLException {
        Database database = Database.getInstance("postgresqldb");

        long result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run();

        assertNotEquals(-1, result);

        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run();

        assertNotEquals(-1, result);

        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run();

        assertNotEquals(-1, result);

        postgreSQLSelectTest();
    }

    @Test
    public void postgreSQLFormatSelect(){
        Database database = Database.getInstance("postgresqldb");

        Select<?> select = database.select()
                .from("SOME_TABLE")
                .where("id", 24);

        String selectQuery = select.toString();

        assertNotNull(selectQuery);

        System.out.println(selectQuery);

        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24".length(), selectQuery.length());
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24", selectQuery);
    }


}
