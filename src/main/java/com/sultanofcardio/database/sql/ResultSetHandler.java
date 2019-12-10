package com.sultanofcardio.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
    void handle(ResultSet resultSet) throws SQLException;
}
