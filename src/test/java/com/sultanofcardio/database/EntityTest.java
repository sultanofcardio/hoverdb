package com.sultanofcardio.database;

import com.sultanofcardio.database.models.BasicEntity;
import com.sultanofcardio.database.sql.FileDatabase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static com.sultanofcardio.database.sql.types.Types.SQLite;

public class EntityTest {

    @BeforeClass
    public static void setup() throws SQLException {
        FileDatabase sqlitedb = FileDatabase.connect("sqlite.db", SQLite, "sqlitedb");
        sqlitedb.run("CREATE TABLE IF NOT EXISTS test_table ( id INTEGER PRIMARY KEY AUTOINCREMENT, words VARCHAR(50) )");
        sqlitedb.run(String.format("INSERT INTO test_table(words) values('The time is now %s')", System.currentTimeMillis()));
    }

    @AfterClass
    public static void tearDown(){

    }

    @Test
    public void testLoad(){
        FileDatabase db = FileDatabase.getInstance("sqlitedb");
        BasicEntity basicEntity = null;
        try {
            basicEntity = Entity.load(BasicEntity.class, db, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(basicEntity);
    }

    @Test
    public void testLoadAll(){
        FileDatabase db = FileDatabase.getInstance("sqlitedb");
        List<BasicEntity> basicEntity = null;
        try {
            basicEntity = BasicEntity.loadAll(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(basicEntity);
    }

    @Test
    public void testConstruct(){
        FileDatabase db = FileDatabase.getInstance("sqlitedb");
        BasicEntity basicEntity = new BasicEntity(db, "Words");
        System.out.println(basicEntity);
    }

}
