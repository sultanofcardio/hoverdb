package com.sultanofcardio.database;

import com.sultanofcardio.database.sql.Database;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract class representing a table in a database.
 *
 * @param <T> the type of your entity class
 */
public abstract class Entity<T extends Entity<T>> {

    private Database database;

    private String tableName;

    /**
     * Create a new instance of this entity on its database. The entity will not be saved until
     * you call the {@link #save()} method
     * @param database The database on which to create the entity
     */
    protected Entity(Database database) {
        this.database = database;
        this.tableName = getClass().getName();
        if(getClass().getDeclaredAnnotation(EntityName.class) != null){
            this.tableName = getClass().getDeclaredAnnotation(EntityName.class).value();
        }
    }

    public Database getDatabase() {
        return database;
    }

    public Entity<T> setDatabase(Database database) {
        this.database = database;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Get an instance of an entity from the database
     * @param type The type of the entity to load
     * @param database The database from which to load
     * @param args The arguments to load by
     * @param <T> The type upper bound on the entity (must actually be an entity)
     * @return The loaded entity
     * @throws Exception if any error occurs while loading, or if the entity is not found
     */
    public static <T extends Entity<T>> T load(Class<T> type, Database database, Object... args) throws Exception{
        Constructor<T> dBConstructor;
        try {
            dBConstructor = type.getDeclaredConstructor(Database.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Required constructor not found. Please add public %s(Database database){...}", type.getSimpleName()));
        }

        dBConstructor.setAccessible(true);
        return dBConstructor.newInstance(database).load(args);
    }

    /**
     * Load an instance of this entity from the database
     * @param args identifying arguments
     * @return this entity, populated from the database
     * @throws SQLException if any error occurs while loading, or if the entity is not found
     */
    public abstract T load(Object... args) throws SQLException;

    /**
     * Load all instances of this entity from the database
     * @param args identifying arguments
     * @return a collection of all instances of this entity contained in the database
     * @throws SQLException if any error occurs while loading, or if the entity is not found
     */
    public abstract List<T> loadAll(Object... args) throws SQLException;

    /**
     * Insert this entity into the database as a new record
     * @return number of rows affected by save action; -1 if it failed
     */
    public abstract long save();

    /**
     * Update this entity's record in the database
     * @return number of rows affected by update action; -1 if it failed
     */
    public abstract long update();

    /**
     * Delete this entity's record from the database
     * @return number of rows affected by delete action; -1 if it failed
     */
    public abstract long delete();

    public abstract String toString();

}
