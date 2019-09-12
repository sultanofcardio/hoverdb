package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.types.DatabaseType;
import org.intellij.lang.annotations.Language;

import java.util.*;

/**
 * Class representing an instance of an SQL update query
 * @param <T> Optional type parameter of your subclass
 */
@SuppressWarnings({"Duplicates", "unchecked"})
public class Update<T extends Update<?>> extends Statement<Update<T>> {
    protected Map<String, Object> setConditions;
    protected List<String> stringSetConditions;

    /**
     * Create a new UPDATE query
     * @param tableName the table to modify
     */
    public Update(String tableName) {
        super(Type.DML);
        setTableName(tableName);
        setConditions = new HashMap<>();
        stringSetConditions = new ArrayList<>();
    }

    /**
     * Specify a column name-value pair to be injected into this update statement
     * @param column The name of the column to be set
     * @param value The value to set to the named column
     * @return this update statement
     */
    public T set(String column, Object value){
        setConditions.put(escape(column), value);
        return (T) this;
    }

    /**
     * Specify several pairs of column names and values for injecting into the update statement
     * @param values pairs of column names and values
     * @return this update statement
     */
    public T set(Map<String, Object> values){
        for(String key: values.keySet())
            setConditions.put(escape(key), values.get(key));

        return (T) this;
    }

    /**
     * Specify a raw SQL set expression, of the form <code>SET column = value</code>.
     * Do not include any commas in this expression
     * @param condition valid SQL set expression
     * @return this update statement
     */
    public T set(@Language("SQL")String condition){
        stringSetConditions.add(condition);
        return (T) this;
    }

    /**
     * Get a view of the name-value mappings in this update statement. This view cannot be modified.
     * @return a map view
     */
    public Map<String, Object> getSetConditions() {
        return Collections.unmodifiableMap(setConditions);
    }

    /**
     * Specify several pairs of column names and values for injecting into the update statement, replacing then
     * current pair mappings. An empty list will remove all mappings
     * @param setConditions pairs of column names and values
     * @return this update statement
     */
    public T setSetConditions(Map<String, Object> setConditions) {
        this.setConditions = new HashMap<>(setConditions);
        return (T) this;
    }

    /**
     * Get a view of the list of raw SQL set expressions in this update statement. This view cannot be modified.
     * @return a list view
     */
    public List<String> getStringSetConditions() {
        return Collections.unmodifiableList(stringSetConditions);
    }

    /**
     * Specify several raw SQL set expressions for injecting into the update statement, replacing the current list
     * of such expressions. An empty list will remove all expressions
     * @param stringSetConditions pairs of column names and values
     * @return this update statement
     */
    public T setStringSetConditions(List<String> stringSetConditions) {
        this.stringSetConditions = new ArrayList<>(stringSetConditions);
        return (T) this;
    }

    /**
     * @see DatabaseType#formatUpdate(Update)
     */
    @Override
    protected String format() {
        return database.getDatabaseType().formatUpdate(this);
    }
}
