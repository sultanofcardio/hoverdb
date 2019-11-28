package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.types.DatabaseType;
import org.intellij.lang.annotations.Language;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Abstract representation of a statement in a RDBMS.
 * @author sultanofcardio
 */
@SuppressWarnings({"Duplicates", "unchecked", "WeakerAccess", "UnusedReturnValue"})
public abstract class Statement<T extends Statement<?>> {
    protected Type type;
    protected String tableName;
    protected Map<String, Object> whereConditions;
    protected List<String> stringWhereConditions, genericConditions;
    protected Database database;

    public enum Type {
        /**
         * Data Definition Language Statement
         */
        DDL,

        /**
         * Data Manipulation Language Statement
         */
        DML,

        /**
         * Data Control Language Statement
         */
        DCL,

        /**
         * Transaction Control Statement
         */
        TCS
    }

    public Statement(Type type) {
        this.type = type;
        whereConditions = new HashMap<>();
        stringWhereConditions = new ArrayList<>();
        genericConditions = new ArrayList<>();
    }

    public T from(String tableName){
        return setTableName(tableName);
    }

    public T where(String column, Object value){
        whereConditions.put(escape(column), value);
        return (T) this;
    }

    public T where(Map<String, Object> whereConditions){

        for(String key: whereConditions.keySet()){
            this.whereConditions.put(escape(key), whereConditions.get(key));
        }

        return (T) this;
    }

    public T where(@Language("SQL")String condition){
        stringWhereConditions.add(condition);
        return (T) this;
    }

    public T where(@Language("SQL")String... conditions){
        stringWhereConditions.addAll(Arrays.asList(conditions));
        return (T) this;
    }

    public T and(String column, Object value){
        whereConditions.put(escape(column), value);
        return (T) this;
    }

    public T and(@Language("SQL")String condition){
        stringWhereConditions.add(condition);
        return (T) this;
    }

    /**
     * Add a generic SQL condition to this query. Don't use this for <strong>WHERE</strong>
     * or <strong>LIMIT</strong>, use one of the convenience where methods or {@link Select#limit(int)} instead
     * @param condition The condition as SQL
     * @return An instance of this query
     */
    public T condition(@Language("SQL")String condition){
        genericConditions.add(condition);
        return (T) this;
    }

    public T setDatabase(Database database){
        this.database = database;
        return (T) this;
    }

    public String getTableName() {
        return tableName;
    }

    public DatabaseType getDatabaseType() {
        return database.getDatabaseType();
    }

    public Map<String, Object> getWhereConditions() {
        return whereConditions;
    }

    public List<String> getStringWhereConditions() {
        return stringWhereConditions;
    }

    public List<String> getGenericConditions() {
        return genericConditions;
    }

    public Database getDatabase() {
        return database;
    }

    public Type getType() {
        return type;
    }

    /**
     * Format this Statement object as a valid raw SQL string
     * @return valid SQL code
     */
    protected abstract String format();

    protected T setTableName(String tableName){
        this.tableName = escape(tableName);
        return (T) this;
    }

    /**
     * Escapes any {@literal '} characters in the string representation of the input
     * @param value The input to be escaped
     * @return The escaped value
     */
    public static String escape(Object value){
        String sValue = value == null? "null" : value.toString();

        if(value != null && Date.class.isAssignableFrom(value.getClass()))
            sValue = formatDate((Date)value);

        return sValue.replace("'", "''");
    }

    public String toString(){
        return format();
    }

    public static String formatDate(Date date){
        String dateFormat = com.sultanofcardio.database.sql.Date.DEFAULT_FORMAT;
        if (date.getClass().getDeclaredAnnotation(com.sultanofcardio.database.sql.Date.class) != null){
            dateFormat = date.getClass().getDeclaredAnnotation(com.sultanofcardio.database.sql.Date.class).value();
        }

        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * Run this statement on its internal database
     * @return the result of this statement
     * @see Database#run(Statement)
     */
    public long run(){
        return database.run(this);
    }
}
