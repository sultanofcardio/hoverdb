package com.sultanofcardio.database

import com.sultanofcardio.database.sql.Database
import com.sultanofcardio.database.sql.FileDatabase
import com.sultanofcardio.database.sql.types.Types
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.sql.ResultSet
import java.sql.SQLException
import org.h2.tools.Server

class H2FileTest {

    private val database = Database.getInstance("h2db")

    @Test
    @Throws(SQLException::class)
    fun h2DeleteTest() {
        h2InsertTest()
        val deleted = database.delete()
                .from("test_table")
                .where("id", 1)
                .run()

        assert(deleted == 1L)

        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    Assert.assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2UpdateTest() {
        h2InsertTest()
        val updated = database.update("test_table")
                .set("words", "Changed this one")
                .where("id", 1)
                .run()

        assert(updated == 1L)

        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    Assert.assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2SelectTest() {
        h2InsertTest()
        database.select()
                .from("test_table")
                .execute { resultSet: ResultSet ->
                    Assert.assertNotNull(resultSet)
                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val words = resultSet.getString("words")
                        println(String.format("Row{id=%s, words=%s}", id, words))
                    }
                }
    }

    @Test
    @Throws(SQLException::class)
    fun h2InsertTest() {
        var result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        Assert.assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        Assert.assertNotEquals(-1, result)
        result = database.insert()
                .into("test_table")
                .value("words", String.format("The time is now %s", System.currentTimeMillis()))
                .run()
        Assert.assertNotEquals(-1, result)
    }

    @Test
    fun h2FormatSelect() {
        val select = database.select()
                .from("SOME_TABLE")
                .where("id", 24)
        val selectQuery = select.toString()
        println(selectQuery)
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24".length.toLong(), selectQuery.length.toLong())
        Assert.assertEquals("SELECT * FROM SOME_TABLE WHERE id = 24", selectQuery)
    }

    companion object {

        lateinit var databaseServer: Server

        @BeforeClass
        @JvmStatic
        fun setup() {
            databaseServer = Server.createTcpServer("-tcpAllowOthers").start()
            FileDatabase.connect("h2db", Types.H2File, "h2db")
                    .run("CREATE TABLE test_table( id integer primary key auto_increment, words varchar);")
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            FileDatabase.getInstance("h2db").run("DROP ALL OBJECTS;")
            databaseServer.stop()
        }
    }
}
