package com.sultanofcardio.database;

import com.sultanofcardio.database.sql.FileDatabase;
import com.sultanofcardio.database.sql.statement.Select;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sultanofcardio.database.sql.types.Types.SQLite;
import static org.junit.Assert.*;

@SuppressWarnings("SqlDialectInspection")
public class SQLiteTest {

    @BeforeClass
    public static void setup() {
        FileDatabase sqldb = FileDatabase.connect("sqlite.db", SQLite, "sqlitedb");
        sqldb.run("CREATE TABLE IF NOT EXISTS test_table ( id INTEGER PRIMARY KEY AUTOINCREMENT, words VARCHAR(50) )");
        sqldb.run(String.format("INSERT INTO test_table(words) values('The time is now %s')", System.currentTimeMillis()));
    }

    @AfterClass
    public static void tearDown(){

    }

    @Test
    public void sqliteSelectTest() throws SQLException {
        FileDatabase database = FileDatabase.getInstance("sqlitedb");

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
    public void sqliteInsertTest() throws SQLException {
        FileDatabase database = FileDatabase.getInstance("sqlitedb");

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

        sqliteSelectTest();
    }

    @Test
    public void sqliteFormatSelect(){
        FileDatabase database = FileDatabase.getInstance("sqlitedb");

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


}
