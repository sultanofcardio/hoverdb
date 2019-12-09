package com.sultanofcardio.database.sql

import java.sql.ResultSet
import java.sql.Statement

data class ResourceSet(val resultSet: ResultSet?, val statement: Statement?) {
    fun close(){
        resultSet?.close()
        statement?.close()
    }
}