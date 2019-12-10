package com.sultanofcardio.database;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.statement.Select;
import org.junit.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sultanofcardio.database.sql.types.Types.MySQL;
import static org.junit.Assert.*;

public class MySQLTest {

    private static DB mysql;

    @BeforeClass
    public static void setup() {
        try {
            mysql = DB.newEmbeddedDB(3307);
            mysql.start();
            mysql.createDB("db", "user", "");
            mysql.run("CREATE TABLE test_table ( id INT NOT NULL AUTO_INCREMENT, words VARCHAR(50), PRIMARY KEY (id))",
                    "user", "", "db");
            mysql.run(String.format("INSERT INTO test_table(words) values('The time is now %s')", System.currentTimeMillis()),
                    "user", "", "db");
            Database.connect("db", MySQL, "localhost",
                    "3307", "user", "", "mysqldb");
        } catch (ManagedProcessException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown(){
        try {
            if(mysql != null) mysql.stop();
        } catch (ManagedProcessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mysqlSelectTest() throws SQLException {
        Database database = Database.getInstance("mysqldb");

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
    public void mysqlInsertTest() throws SQLException {
        Database database = Database.getInstance("mysqldb");

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

        mysqlSelectTest();
    }

    @Test
    public void mysqlFormatSelect(){
        Database database = Database.getInstance("mysqldb");

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
