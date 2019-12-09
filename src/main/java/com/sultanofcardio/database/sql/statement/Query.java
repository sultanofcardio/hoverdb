package com.sultanofcardio.database.sql.statement;

import com.sultanofcardio.database.sql.Database;
import com.sultanofcardio.database.sql.ResourceSet;
import com.sultanofcardio.database.sql.ResultSetHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract representation of a query in a RDBMS.
 * @author sultanofcardio
 */
@SuppressWarnings("WeakerAccess")
public abstract class Query<T extends Query<?>> extends Statement<T> {

    /**
     *Create a new instance of this query
     * @param type The query type
     */
    protected Query(Type type) {
        super(type);
        this.type = type;
        whereConditions = new HashMap<>();
        stringWhereConditions = new ArrayList<>();
        genericConditions = new ArrayList<>();
    }

    /**
     * Execute this query on its internal database
     * @return the result of this query
     * @see Database#execute(Query)
     */
    public ResourceSet execute(){
        return database.execute(this);
    }

    /**
     * Execute this query on its internal database with a handler to capture results
     * @see Database#execute(Query)
     */
    public void execute(ResultSetHandler handler){
        database.execute(this, handler);
    }
}
