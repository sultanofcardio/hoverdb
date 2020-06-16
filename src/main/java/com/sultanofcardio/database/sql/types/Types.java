package com.sultanofcardio.database.sql.types;

/**
 * Convenience constants class containing the built in database types
 */
public class Types {
    /**
     * MySQL database server support
     */
    public static final MySQL MySQL = new MySQL();

    /**
     * SQLServer database server support
     */
    public static final SQLServer SQLServer = new SQLServer();

    /**
     * Oracle database server support
     */
    public static final Oracle Oracle = new Oracle();

    /**
     * PostgreSQL database server support
     */
    public static final PostgreSQL PostgreSQL = new PostgreSQL();

    /**
     * SQLite file database support
     */
    public static final SQLite SQLite = new SQLite();

    /**
     * H2 file database support
     */
    public static final DatabaseType H2File = com.sultanofcardio.database.sql.types.H2File.INSTANCE;

    /**
     * H2 in-memory database support
     */
    public static final DatabaseType H2Mem = com.sultanofcardio.database.sql.types.H2Mem.INSTANCE;
}
