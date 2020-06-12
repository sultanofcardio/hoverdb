package com.sultanofcardio.database.sql

fun <T> Database.transaction(work: Database.() -> T): T {
    val clone = Database(schema, databaseType, host, port, username, password)
    clone.getConnection().autoCommit = false

    try {
        val t = work(clone)
        clone.connection.commit()
        clone.connection.close()
        return t
    } catch (e: Throwable) {
        clone.connection.rollback()
        clone.connection.close()
        throw e
    }
}