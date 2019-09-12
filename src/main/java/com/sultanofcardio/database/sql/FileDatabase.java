package com.sultanofcardio.database.sql;


import com.sultanofcardio.database.sql.types.DatabaseType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.sultanofcardio.database.sql.types.Types;

/**
 * This class represents a connection to a file database in a file system. <br><br>
 *
 * @author sultanofcardio
 *
 * @see Database
 */
public final class FileDatabase extends Database {
    private String path;

    /**
     * Create a new FileDatabase instance
     * @param schema The name of the database schema
     * @param type Type of database (e.g. {@link Types#SQLite})
     */
    private FileDatabase(String schema, DatabaseType type) {
        super(schema, type);
    }

    /**
     * Get an instance of this Database connector. A cached instance will be returned if one already exists
     * @param path path to the database file
     * @param type The type of the file database
     * @param alias The name that the database will be cached under a la {@link #getInstance(String)}
     * @return instance of the database requested
     */
    public static FileDatabase connect(String path, DatabaseType type, String alias){
        if(path == null || path.isEmpty())
            throw new IllegalArgumentException("path is required");

        if(alias == null || alias.isEmpty())
            throw new IllegalArgumentException("alias is required");

        String[] pathParts = path.split("/");
        String schema, fileName;
        if(pathParts.length > 0){
            fileName = pathParts[pathParts.length-1];
        } else fileName = path;

        if(fileName.contains(".")){
            schema = fileName.substring(0, fileName.lastIndexOf("."));
        } else schema = fileName;

        FileDatabase database = getInstance(alias);
        if(database == null) database = new FileDatabase(schema, type);

        database.setPath(path);
        cacheDatabase(alias, database);

        return database;
    }

    /**
     * Get an instance of a cached {@link FileDatabase} connection you previously created.
     * @param alias the alias of the database you supplied when creating the connection
     * @return the database instance, or null if none exists
     */
    public static FileDatabase getInstance(String alias){
        FileDatabase fileDb = null;
        Database database = manager.get(alias);
        if(database instanceof FileDatabase){
                fileDb = (FileDatabase) database;
        }

        return fileDb;
    }

    @Override
    public Connection getConnection(){
        try {
            if(this.connection == null || this.connection.isClosed()) {
                this.connection = null;
                Class.forName(databaseType.getDriverName());
                connection = DriverManager.getConnection(databaseType.getConnectionString(path));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Get the file path to this file database
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the file path to the file database
     * @param path file path
     * @return this file database
     */
    public FileDatabase setPath(String path) {
        this.path = path;
        return this;
    }
}
