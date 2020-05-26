package com.sultanofcardio.database.sql.types;

import com.sultanofcardio.database.sql.statement.Delete;
import com.sultanofcardio.database.sql.statement.Insert;
import com.sultanofcardio.database.sql.statement.Select;
import com.sultanofcardio.database.sql.statement.Update;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sultanofcardio.database.sql.statement.Statement.escape;

/**
 * Support for database connections to PostgreSQL database servers
 *
 * TODO: Implement
 */
@SuppressWarnings("DuplicatedCode")
public final class PostgreSQL extends DatabaseType {

    PostgreSQL(){
        super("PostgreSQL", "jdbc:postgresql://%1$s:%2$s/%3$s", "org.postgresql.Driver");
    }

    /**
     * Arguments host, port and schema are mandatory in that order
     * @param args The comma separated host, port and schema
     * @return A formatted connection string
     */
    @Override
    public String getConnectionString(String... args) {
        String host = args[0];
        String port = args[1];
        String schema = args[2];
        return String.format(connectionString, host, port, schema);
    }

    @Override
    public String formatSelect(Select<?> select) {
        StringBuilder result = new StringBuilder(select.isDistinct() ? "SELECT DISTINCT " : "SELECT ");
        String[] columns = select.getColumns();
        if(columns != null && columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                if (i != columns.length - 1)
                    result.append(String.format("%s, ", escape(column)));
                else
                    result.append(String.format("%s ", escape(column)));
            }
        } else {
            result.append("* ");
        }

        String tableName = select.getTableName();

        if(tableName == null || tableName.isEmpty())
            throw new IllegalStateException("Table name not specified");

        result.append(String.format("FROM %s ", tableName));

        Map<String, Object> whereConditions = select.getWhereConditions();

        boolean whereAppended = false;

        if(whereConditions != null && !whereConditions.isEmpty()){
            result.append("WHERE ");
            whereAppended = true;
            appendConditions(whereConditions, result);
        }

        List<String> stringWhereConditions = select.getStringWhereConditions();

        if(stringWhereConditions != null && !stringWhereConditions.isEmpty()){
            if(!whereAppended) {
                result.append("WHERE ");
                whereAppended = true;
            } else result.append("AND ");

            appendConditions(stringWhereConditions, result);
        }

        appendGenericConditions(select, result, !whereAppended);

        List<String> orderBy = select.getOrderBy();

        if(orderBy != null && orderBy.size() > 0){
            result.append("ORDER BY ");
            for(int i=0; i < orderBy.size(); i++) {
                String orderByCondition = orderBy.get(i);
                result.append(orderByCondition);

                if(i != orderBy.size() - 1)
                    result.append(", ");

            }
        }

        if(select.getLimit() != -1){
            result.append(String.format(" LIMIT %s ", select.getLimit()));
        }

        return result.toString().trim();
    }

    @Override
    public String formatUpdate(Update<?> update) {
        String tableName = update.getTableName();

        if(tableName == null || tableName.isEmpty())
            throw new IllegalStateException("Table name not specified");

        StringBuilder result = new StringBuilder(String.format("UPDATE %s SET ", tableName));

        Map<String, Object> setConditions = update.getSetConditions();

        Iterator<String> columnNameIterator = setConditions.keySet().iterator();

        if(setConditions.isEmpty())
            throw new IllegalStateException("No column values found to modify");

        while (columnNameIterator.hasNext()){
            String columnName = columnNameIterator.next();
            Object value = setConditions.get(columnName);

            String formatString = (value != null && (String.class.isAssignableFrom(value.getClass()) ||
                    java.util.Date.class.isAssignableFrom(value.getClass()))) ? "%s = '%s'" : "%s = %s";

            if(columnNameIterator.hasNext())
                result.append(String.format(String.format("%s, ", formatString), columnName, escape(value)));
            else
                result.append(String.format(String.format("%s ", formatString), columnName, escape(value)));
        }

        List<String> stringSetConditions = update.getStringSetConditions();

        if(stringSetConditions.size() > 0){
            result.append(", ");
        }

        for(int i=0; i<stringSetConditions.size(); i++){
            String condition = stringSetConditions.get(i);

            if(i != stringSetConditions.size() - 1) {
                result.append(String.format("%s, ", condition));
            } else {
                result.append(String.format("%s ", condition));
            }
        }

        Map<String, Object> whereConditions = update.getWhereConditions();

        boolean whereAppended = false;

        if(whereConditions != null && !whereConditions.isEmpty()){
            result.append("WHERE ");
            whereAppended = true;
            appendConditions(whereConditions, result);
        }

        List<String> stringWhereConditions = update.getStringWhereConditions();

        if(stringWhereConditions != null && !stringWhereConditions.isEmpty()){
            if(!whereAppended) {
                result.append("WHERE ");
                whereAppended = true;
            } else result.append("AND ");

            appendConditions(stringWhereConditions, result);
        }

        appendGenericConditions(update, result, whereAppended);

        return result.toString().trim();
    }

    @Override
    public String formatInsert(Insert<?> insert) {
        String tableName = insert.getTableName();

        if(tableName == null || tableName.isEmpty())
            throw new IllegalStateException("Table name not specified");

        Map<String, Object> columnValues = insert.getColumnValues();

        if(columnValues.isEmpty())
            throw new IllegalStateException("No values found to be inserted");

        StringBuilder result = new StringBuilder(String.format("INSERT INTO %s(", tableName));

        String[] columnNames = columnValues.keySet().toArray(new String[]{});

        StringBuilder valuesString = new StringBuilder("VALUES(");

        for (int i=0; i<columnNames.length; i++){
            String columnName =  columnNames[i];

            if(i != columnNames.length - 1)
                result.append(String.format("%s, ", columnName));
            else
                result.append(String.format("%s) ", columnName));

            Object value =  columnValues.get(columnNames[i]);

            String formatString = (value != null && (String.class.isAssignableFrom(value.getClass()) ||
                    java.util.Date.class.isAssignableFrom(value.getClass()))) ? "'%s'" : "%s";

            if(i != columnNames.length - 1)
                valuesString.append(String.format("%s, ", String.format(formatString, escape(value))));
            else
                valuesString.append(String.format("%s)", String.format(formatString, escape(value))));
        }

        return result.append(valuesString).toString().trim();
    }

    @Override
    public String formatDelete(Delete<?> delete) {
        String tableName = delete.getTableName();

        if(tableName == null || tableName.isEmpty())
            throw new IllegalStateException("Table name not specified");

        StringBuilder result = new StringBuilder(String.format("DELETE FROM %s ", tableName));

        Map<String, Object> whereConditions = delete.getWhereConditions();

        boolean whereAppended = false;

        if(whereConditions != null && !whereConditions.isEmpty()){
            result.append("WHERE ");
            whereAppended = true;
            appendConditions(whereConditions, result);
        }

        List<String> stringWhereConditions = delete.getStringWhereConditions();

        if(stringWhereConditions != null && !stringWhereConditions.isEmpty()){
            if(!whereAppended) {
                result.append("WHERE ");
                whereAppended = true;
            } else result.append("AND ");

            appendConditions(stringWhereConditions, result);
        }

        appendGenericConditions(delete, result, !whereAppended);

        return result.toString().trim();
    }
}
