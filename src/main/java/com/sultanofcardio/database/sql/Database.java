package com.sultanofcardio.database.sql;

import com.sultanofcardio.database.sql.statement.Statement;
import com.sultanofcardio.database.sql.statement.*;
import com.sultanofcardio.database.sql.types.DatabaseType;
import org.intellij.lang.annotations.Language;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class represents a connection to a database in a Relational Database Management System (RDBMS). <br><br>
 *
 * Instances are created using {@link #connect(String, DatabaseType, String, String, String, String, String)} <br><br>
 *
 * Each time an instance is created for a previously unknown database, that instance is cached,
 * after which it can be retrieved using {@link Database#getInstance(String)}.
 *
 * In its current form, only DML statements may be executed by the convenience methods in this class. Other
 * statements are facilitated by allowing the executing or arbitrary SQL using the {@link #run(String)} and
 * {@link #execute(String)} methods<br><br>
 *
 * Transactions are not yet supported
 *
 * @author sultanofcardio
 */
@SuppressWarnings("WeakerAccess")
public class Database {
    protected String schema;
    protected String host;
    protected String port;
    protected String username;
    protected String password;
    protected DatabaseType databaseType;
    protected Properties properties;
    protected Connection connection;
    protected static Map<String, Database> manager = new HashMap<>();

    /**
     * Get an instance of this Database connector.
     * @param schema the name of the database being used
     * @param type the type of the database management system
     * @param host hostname or IP address
     * @param port port number
     * @param username the user with which to connect
     * @param password the password of the database user. Defaults to empty string if null is passed
     */
    protected Database(String schema, DatabaseType type, String host, String port, String username, String password) {
        this.schema = schema;
        this.databaseType = type;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.properties = new Properties();
        this.connection = null;
        addProperty("user", username);
        addProperty("password", password);
    }

    /**
     * Get an instance of this Database connector.
     * @param schema the name of the database being used
     * @param type the type of the database management system
     */
    protected Database(String schema, DatabaseType type) {
        this.schema = schema;
        this.databaseType = type;
        this.properties = new Properties();
        this.connection = null;
    }

    /**
     * Get an instance of this Database connector. A cached instance will be returned if one already exists
     * @param schema the name of the database being used
     * @param type the type of the database management system
     * @param host hostname or IP address
     * @param port port number
     * @param username the user with which to connect
     * @param password the password of the database user. Defaults to empty string if null is passed
     * @param alias The name that the database will be cached under a la {@link #getInstance(String)}
     * @return instance of the database requested
     */
    public static Database connect(String schema, DatabaseType type, String host, String port,
                                   String username, String password, String alias){
        if(schema == null || schema.isEmpty())
            throw new IllegalArgumentException("Database name is required");

        if(type == null)
            throw new IllegalArgumentException("type is required");

        if(host == null || host.isEmpty())
            throw new IllegalArgumentException("host is required");

        if(port == null || port.isEmpty())
            throw new IllegalArgumentException("port is required");

        if(username == null || username.isEmpty())
            throw new IllegalArgumentException("username is required");

        if(alias == null || alias.isEmpty())
            throw new IllegalArgumentException("alias is required");

        if(password == null)
            password = "";

        Database database = getInstance(alias);
        if(database == null) database = new Database(schema, type, host, port, username, password);
        cacheDatabase(alias, database);
        return database;
    }

    /**
     * Get an instance of a cached {@link Database} connection you previously created.
     * @param alias the alias of the database you supplied when creating the connection
     * @return the database instance, or null if none exists
     */
    public static Database getInstance(String alias){
        return manager.get(alias);
    }

    /**
     * Cache a database connection by name for later re-use. An existing connection by the same alias will be
     * overwritten, if it exists
     * @param name The name of the database
     * @param database The database instance.
     */
    protected static void cacheDatabase(String name, Database database){
        manager.put(name, database);
    }

    /**
     * Retrieve the connection object for this database.
     * This connection is maintained for the life of the JVM and you are limited to one connection per database
     * @return A connection instance, or null if a connection cannot be established
     */
    public Connection getConnection(){
        try {
            if(this.connection == null || this.connection.isClosed()) {
                this.connection = null;
                Class.forName(databaseType.getDriverName());
                connection = DriverManager.getConnection(databaseType.getConnectionString(host, port, schema),
                        properties);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Execute a raw SQL query that returns a result set
     * @param sql Valid SQL code
     * @return The result of the query
     * @see #run(String)
     */
    public ResourceSet execute(@Language("SQL") String sql){
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if(statement != null){
                try {statement.close(); } catch (Exception ignored){}
            }

            try {connection.close(); } catch (Exception ignored){}
        }

        return new ResourceSet(resultSet, statement);
    }

    /**
     * Execute a query object
     * @param query A query object that formats to valid SQL code
     * @return The result of the query
     * @see #run(Statement)
     */
    public ResourceSet execute(Query<?> query){
        return execute(query.toString());
    }

    /**
     * Execute a raw SQL query that auto-closes its resources
     * @param sql Valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see #run(String)
     */
    public void execute(@Language("SQL") String sql, ResultSetHandler resultSetHandler) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        final Connection connection = getConnection();

        try {
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                resultSetHandler.handle(resultSet);
            }
        } finally {
            if(resultSet != null) {
                try {resultSet.close(); } catch (Exception ignored){}
            }

            if(statement != null) {
                try {statement.close(); } catch (Exception ignored){}
            }
        }
    }

    /**
     * Execute a query object. The result set will only be valid inside the handle method of the handler
     * @param query A query object that formats to valid SQL code
     * @param resultSetHandler Handle the result of the query
     * @see #run(Statement)
     */
    public void execute(Query<?> query, ResultSetHandler resultSetHandler) throws SQLException {
        execute(query.toString(), resultSetHandler);
    }

    /**
     * Run a raw SQL statement that modifies the database.
     * @param sql Valid SQL code
     * @return The number of rows affected by the query
     * @see #execute(String)
     */
    public long run(@Language("SQL") String sql) throws SQLException {
        try {
            Class.forName(databaseType.getDriverName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = getConnection();
        PreparedStatement statement = null;
        long rowNum = -1;

        try {
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                rowNum = statement.executeUpdate();

            }
        } finally {

            if (statement != null) {
                try { statement.close(); } catch (Exception ignored){}
            }

            if (connection != null) {
                try { connection.close(); } catch (Exception ignored){}
            }

        }

        return rowNum;
    }

    /**
     * Run a raw SQL query that modifies the database
     * @param statement A statement object that formats to valid SQL code
     * @return The number of rows affected by the query
     * @see #execute(Query)
     */
    public long run(Statement<?> statement) throws SQLException {
        return run(statement.toString());
    }

    /**
     * Initiate a new select query
     * @param columns The columns to select
     * @return The select query instance
     */
    public Select<?> select(String... columns){
        return select(false, columns).setDatabase(this);
    }

    /**
     * Initiate a new select query, specifying whether or not to have distinct results
     * @param columns The columns to select
     * @param distinct Specify whether or not to have distinct results
     * @return The select query instance
     */
    public Select<?> select(boolean distinct, String... columns){
        return new Select<>(distinct, columns).setDatabase(this);
    }

    /**
     * Initiate a new insert query
     * @return The insert query instance
     */
    public Insert<?> insert(){
        return new Insert<>().setDatabase(this);
    }

    /**
     * Initiate a new delete query
     * @return The delete query instance
     */
    public Delete<?> delete(){
        return new Delete<>().setDatabase(this);
    }

    /**
     * Initiate a new update query
     * @param tableName The name of the table to update
     * @return The update query instance
     */
    public Update<?> update(String tableName){
        return new Update<>(tableName).setDatabase(this);
    }

    @Override
    protected void finalize() throws Throwable {
        if(this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    // <editor-fold desc="Getters and setters">

    public String getSchema() {
        return schema;
    }

    public Database setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Database setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPort() {
        return port;
    }

    public Database setPort(String port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Database setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Database setPassword(String password) {
        this.password = password;
        return this;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public Database setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public Database setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Add a single property to this database for use during the connection process
     * @param name The name of the property
     * @param value The value of the property
     * @return this database
     */
    public Database addProperty(String name, Object value){
        properties.put(name, value);
        return this;
    }

    // </editor-fold>
}
