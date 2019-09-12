package com.sultanofcardio.database.sql.types;

import com.sultanofcardio.database.sql.statement.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sultanofcardio.database.sql.statement.Statement.escape;

/**
 * Abstract representation of a particular RDBMS implementation. Extend this class to provide your own implementation
 */
@SuppressWarnings("WeakerAccess")
public abstract class DatabaseType {

    protected String name, connectionString, driverName;

    /**
     * Get a new instance of this database type
     * @param name The name of the database this implementation interfaces with
     * @param connectionString A valid jdbc connection string for this database type
     * @param driverName The fully qualified JDBC driver class name
     */
    protected DatabaseType(String name, String connectionString, String driverName) {
        this.name = name;
        this.connectionString = connectionString;
        this.driverName = driverName;
    }

    /**
     * Get the name of the database this implementation interfaces with
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the fully qualified JDBC driver class name of this implementation
     * @return driver class name
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Specify in your documentation what order your arguments should be passed in
     * @param args The arguments to your connection string
     * @return A formatted connection string
     */
    public abstract String getConnectionString(String... args);

    void appendConditions(Map<String, Object> conditions, StringBuilder result) {
        Iterator<String> keyIterator = conditions.keySet().iterator();
        while (keyIterator.hasNext()){
            String condition =  keyIterator.next();
            Object value = conditions.get(condition);

            String formatString = "%s = ";

            if((String.class.isAssignableFrom(value.getClass()) ||
                    java.util.Date.class.isAssignableFrom(value.getClass()))){
                formatString += "'%s'";
            } else {
                formatString += "%s";
            }

            if(keyIterator.hasNext()){
                result.append(String.format(String.format("%s AND ", formatString), condition, escape(value)));
            } else {
                result.append(String.format(String.format("%s ", formatString), condition, escape(value)));
            }
        }
    }

    void appendConditions(List<String> conditions, StringBuilder result) {
        Iterator<String> valueIterator = conditions.iterator();
        while (valueIterator.hasNext()){
            String condition =  valueIterator.next();

            if(valueIterator.hasNext()){
                result.append(String.format("%s AND ", condition));
            } else {
                result.append(String.format("%s ", condition));
            }
        }
    }

    void appendGenericConditions(Statement<?> query, StringBuilder result, boolean appendWhere) {
        List<String> genericConditions = query.getGenericConditions();
        if(genericConditions.size() > 0) {
            if(appendWhere) result.append("WHERE ");
            else result.append("AND ");
        }

        for(int i = 0; i< genericConditions.size(); i++){
            String condition = genericConditions.get(i);

            if(i != 0){
                result.append("AND ");
            }

            if(i != genericConditions.size() - 1) {
                result.append(String.format("%s AND ", condition));
            } else {
                result.append(String.format("%s ", condition));
            }
        }
    }

    /**
     * Format a valid select query for your RDBMS
     * @param select the select query
     * @return The formatted query
     */
    public abstract String formatSelect(Select<?> select);

    /**
     * Format a valid update query for your RDBMS
     * @param update the update query
     * @return The formatted query
     */
    public abstract String formatUpdate(Update<?> update);

    /**
     * Format a valid insert query for your RDBMS
     * @param insert the insert query
     * @return The formatted query
     */
    public abstract String formatInsert(Insert<?> insert);

    /**
     * Format a valid delete query for your RDBMS
     * @param delete the delete query
     * @return The formatted query
     */
    public abstract String formatDelete(Delete<?> delete);

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass());
    }
}
