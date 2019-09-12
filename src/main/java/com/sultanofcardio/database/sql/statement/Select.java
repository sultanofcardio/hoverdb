package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.types.DatabaseType;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class representing an instance of an SQL select query
 * @param <T> Optional type parameter of your subclass
 */
@SuppressWarnings({"Duplicates", "unchecked"})
public class Select<T extends Select<?>> extends Query<T> {
    protected String[] columns;
    protected int limit;
    protected List<String> orderBy;
    protected boolean distinct = false;

    /**
     * Create a new SELECT query
     * @param columns A list of columns to query from the table. Leave blank to SELECT *
     */
    public Select(String... columns) {
        super(Type.DML);
        this.columns = columns;
        limit = -1;
        this.orderBy = new ArrayList<>();
    }

    /**
     * Create a new SELECT query
     * @param columns A list of columns to query from the table. Leave blank to SELECT *
     * @param distinct Set whether this query should return distinct values
     */
    public Select(boolean distinct, String... columns) {
        this(columns);

        if(distinct && columns.length < 1)
            throw new IllegalArgumentException("Must supply at least one column when using distinct");

        this.distinct = distinct;
    }

    /**
     * Set whether this query should return distinct values
     * @return this query
     */
    public T distinct(){
        if(columns.length < 1)
            throw new IllegalArgumentException("Must supply at least one column when using distinct");

        this.distinct = true;
        return (T) this;
    }

    /**
     * Limit the number of rows returned by this query
     * @param numRows The number of rows desired
     * @return An instance of this query
     */
    public T limit(int numRows){
        this.limit = numRows;
        return (T) this;
    }

    /**
     * Add an order by clause
     * @param orderBy SQL order by clause
     * @return An instance of this query
     */
    public T orderBy(@Language("SQL")String orderBy){
        this.orderBy.add(orderBy);
        return (T) this;
    }

    /**
     * Get a view of the list of the columns being selected by this query. An empty array
     * is equivalent to selecting all columns. This view cannot be modified
     * @return The list of columns
     */
    public String[] getColumns() {
        return Arrays.copyOf(columns, columns.length);
    }

    /**
     * Get the limit to the number of rows returned by this query, if any
     * @return limit, or -1 for no limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get the list of order by clauses
     * @return the list of order by clauses
     */
    public List<String> getOrderBy() {
        return Collections.unmodifiableList(orderBy);
    }

    /**
     * Check if this query is returning distinct rows
     * @return whether or not the query is returning distinct rows
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @see DatabaseType#formatSelect(Select)
     */
    @Override
    protected String format() {
        return database.getDatabaseType().formatSelect(this);
    }
}
