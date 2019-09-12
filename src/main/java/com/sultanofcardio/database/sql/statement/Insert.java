package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.types.DatabaseType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing an instance of an SQL insert query
 * @param <T> Optional type parameter of your subclass
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public class Insert<T extends Insert<?>> extends Statement<Insert<T>> {
    protected Map<String, Object> columnValues;

    /**
     * Create a new instance of an insert query.
     */
    public Insert() {
        super(Type.DML);
        this.columnValues = new HashMap<>();
    }

    /**
     * Specify the name of the table to insert into
     * @param tableName The table name
     * @return this insert query
     */
    public T into(String tableName){
        return (T) setTableName(tableName);
    }

    /**
     * Specify a pair of column name and value for injecting into the insert statement
     * @param columnName The name of the column to insert into
     * @param value The value to insert into the column
     * @return this insert statement
     */
    public T value(String columnName, Object value){
        columnValues.put(escape(columnName), value);
        return (T) this;
    }

    /**
     * Specify several pairs of column names and values for injecting into the insert statement
     * @param values pairs of column names and values
     * @return this insert statement
     */
    public T values(Map<String, Object> values){
        for(String key: values.keySet()){
            this.columnValues.put(escape(key), values.get(key));
        }

        return (T) this;
    }

    /**
     * Get a view of the name-value mappings in this insert statement. This view cannot be modified.
     * @return a map view
     */
    public Map<String, Object> getColumnValues() {
        return Collections.unmodifiableMap(columnValues);
    }

    /**
     * Specify several pairs of column names and values for injecting into the insert statement, replacing the current
     * pair mappings
     * @param columnValues pairs of column names and values
     * @return this insert statement
     */
    public T setColumnValues(Map<String, Object> columnValues) {
        this.columnValues = columnValues;
        return (T) this;
    }

    /**
     * @see DatabaseType#formatInsert(Insert)
     */
    @Override
    protected String format() {
        return database.getDatabaseType().formatInsert(this);
    }
}
