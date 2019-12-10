package com.sultanofcardio.database;

import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.statement.Insert;
import com.sultanofcardio.database.sql.statement.Select;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static com.sultanofcardio.database.sql.types.Types.Oracle;
import static com.sultanofcardio.database.sql.types.Types.PostgreSQL;
import static org.junit.Assert.*;

public class OracleTest {

    @BeforeClass
    public static void setup() {
        Database.connect("db", Oracle, "localhost",
                "3307", "user", "", "oracledb");
    }

    @AfterClass
    public static void tearDown(){
        
    }

    @Test
    public void oracleSelectTest() throws SQLException {
        Database database = Database.getInstance("oracledb");

        database.select()
                .from("test_table")
                .execute(resultSet -> {
                    assertNotNull(resultSet);

                    while(resultSet.next()){
                        int id = resultSet.getInt("id");
                        String words = resultSet.getString("words");
                        System.out.println(String.format("Row{id=%s, words=%s}", id, words));
                    }
                });
    }

    @Test
    public void oracleInsertTest() throws SQLException {
        Database database = Database.getInstance("oracledb");

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

        oracleSelectTest();
    }

    @Test
    public void oracleFormatSelect(){
        Database database = Database.getInstance("oracledb");

        Select<?> select = database.select()
                .from("SOME_TABLE")
                .where("id", 24)
                .limit(1);

        String selectQuery = select.toString();

        assertNotNull(selectQuery);

        System.out.println(selectQuery);

        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1".length(), selectQuery.length());
        assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24 AND ROWNUM <= 1", selectQuery);
    }

    @Test
    public void oracleFormatInsert(){
        Database database = Database.getInstance("oracledb");

        Insert<?> select = database.insert()
                .into("table")
                .value("column1", "value1")
                .value("column2", 0)
                .value("column3", 0.51)
                .value("column4", false)
                .value("column5", new Date())
                .value("column6", null);

        String selectQuery = select.toString();

        assertNotNull(selectQuery);

        System.out.println(selectQuery);
    }


}
