package com.sultanofcardio.database.sql

import com.sultanofcardio.database.sql.types.DatabaseType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

/**
 * This class represents a connection to an in-memory database. <br></br><br></br>
 *
 * @param schema The name of the database schema
 * @param type Type of database (e.g. [Types.H2Mem])
 *
 * @author sultanofcardio
 *
 * @see Database
 */
class MemoryDatabase internal constructor(schema: String?, type: DatabaseType?) : Database(schema, type) {

    init {
        properties = Properties()
    }

    override fun copy(): Database {
        return MemoryDatabase(schema, databaseType)
    }

    override fun getConnection(): Connection {
        try {
            if (connection == null || connection.isClosed) {
                connection = null
                Class.forName(databaseType.driverName)
                connection = DriverManager.getConnection(databaseType.getConnectionString(schema))
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return connection
    }

    companion object {
        /**
         * Get an instance of this Database connector. A cached instance will be returned if one already exists
         * @param schema schema of the database file
         * @param type The type of the in-memory database
         * @param alias The name that the database will be cached under a la [.getInstance]
         * @return instance of the database requested
         */
        fun connect(schema: String?, type: DatabaseType?, alias: String?): MemoryDatabase {
            require(!(schema == null || schema.isEmpty())) { "schema is required" }
            require(!(alias == null || alias.isEmpty())) { "alias is required" }
            var database = getInstance(alias)
            if (database == null) database = MemoryDatabase(schema, type)
            cacheDatabase(alias, database)
            return database
        }

        /**
         * Get an instance of a cached [MemoryDatabase] connection you previously created.
         * @param alias the alias of the database you supplied when creating the connection
         * @return the database instance, or null if none exists
         */
        fun getInstance(alias: String?): MemoryDatabase? {
            var fileDb: MemoryDatabase? = null
            val database = manager[alias]
            if (database is MemoryDatabase) {
                fileDb = database
            }
            return fileDb
        }
    }
}