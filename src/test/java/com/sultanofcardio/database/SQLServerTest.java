package com.sultanofcardio.database;

import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.statement.Select;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sultanofcardio.database.sql.types.Types.Oracle;
import static com.sultanofcardio.database.sql.types.Types.SQLServer;
import static org.junit.Assert.*;

public class SQLServerTest {

    @BeforeClass
    public static void setup() {
        Database.connect("db", SQLServer, "localhost",
                "3307", "user", "", "sqlserverdb");
    }

    @AfterClass
    public static void tearDown(){
        
    }

    @Test
    public void sqlServerSelectTest() throws SQLException {
        Database database = Database.getInstance("sqlserverdb");

        ResultSet resultSet = database.select()
                .from("test_table")
                .execute();

        assertNotNull(resultSet);

        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String words = resultSet.getString("words");
            System.out.println(String.format("Row{id=%s, words=%s}", id, words));
        }
    }

    @Test
    public void sqlServerInsertTest() throws SQLException {
        Database database = Database.getInstance("sqlserverdb");

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

        sqlServerSelectTest();
    }

    @Test
    public void sqlServerFormatSelect(){
        Database database = Database.getInstance("sqlserverdb");

        Select<?> select = database.select()
                .from("SOME_TABLE")
                .where("id", 24)
                .limit(1);

        String selectQuery = select.toString();

        assertNotNull(selectQuery);

        System.out.println(selectQuery);

        assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24".length(), selectQuery.length());
        assertEquals("SELECT TOP 1 * FROM SOME_TABLE WHERE id = 24", selectQuery);
    }


}
