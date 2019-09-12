package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.types.DatabaseType;

/**
 * Class representing an instance of an SQL delete query
 * @param <T> Optional type parameter of your subclass
 */
@SuppressWarnings("Duplicates")
public class Delete<T extends Delete<?>> extends Statement<Delete<T>> {

    /**
     * Create a new instance of a delete query.
     */
    public Delete() {
        super(Type.DML);
    }

    /**
     * @see DatabaseType#formatDelete(Delete)
     */
    @Override
    protected String format() {
        return database.getDatabaseType().formatDelete(this);
    }
}
