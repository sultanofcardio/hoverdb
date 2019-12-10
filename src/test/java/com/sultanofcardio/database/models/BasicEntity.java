package com.sultanofcardio.database.models;

import com.sultanofcardio.database.Entity;
import com.sultanofcardio.database.EntityName;
import com.sultanofcardio.database.sql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO in a database.
 */
@EntityName("test_table")
public class BasicEntity extends Entity<BasicEntity> {
    private int id;
    private String words;

    public BasicEntity(Database database, String words) {
        super(database);
        this.words = words;
    }

    private BasicEntity(Database database) {
        super(database);
    }

    public int getId() {
        return id;
    }

    public BasicEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getWords() {
        return words;
    }

    public BasicEntity setWords(String words) {
        this.words = words;
        return this;
    }

    public static List<BasicEntity> loadAll(Database database) throws SQLException {
        return new BasicEntity(database, null).loadAll();
    }

    @Override
    public BasicEntity load(Object... args) throws SQLException {
        getDatabase().execute(
                getDatabase()
                        .select()
                        .from(getTableName())
                        .where("id", args[0]),
                resultSet -> {
                    if(resultSet == null || !resultSet.next()){
                        throw new SQLException("Unable to find entity");
                    }

                    this.id = resultSet.getInt("id");
                    this.words = resultSet.getString("words");

                    resultSet.close();
                }
        );

        return this;
    }

    @Override
    public List<BasicEntity> loadAll(Object... args) throws SQLException {
        List<BasicEntity> ts = new ArrayList<>();
        getDatabase().execute(
                getDatabase()
                .select()
                .from(getTableName()),
                resultSet -> {
                    if(resultSet != null){
                        while(resultSet.next()){
                            BasicEntity basicEntity = new BasicEntity(getDatabase(), resultSet.getString("words"));
                            basicEntity.setId(resultSet.getInt("id"));
                            ts.add(basicEntity);
                        }
                    }
                }
        );

        return ts;
    }

    @Override
    public long save() {
        try {
            return getDatabase()
                    .insert()
                    .into(getTableName())
                    .value("words", words)
                    .run();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public long update() {
        return 0;
    }

    @Override
    public long delete() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("BasicEntity{id=%d, words='%s'}", id, words);
    }
}
