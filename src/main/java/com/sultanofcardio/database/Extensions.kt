package com.sultanofcardio.database

import java.sql.ResultSet

/**
 * Transform a ResultSet into an immutable list of objects
 */
fun <R> ResultSet.list(mapper: (ResultSet) -> R): List<R> {
    val list = mutableListOf<R>()
    while(next()) list.add(mapper(this))
    return list
}

/**
 * Transform a ResultSet into an immutable set of objects
 */
fun <R> ResultSet.set(mapper: (ResultSet) -> R): Set<R> {
    val set = mutableSetOf<R>()
    while(next()) set.add(mapper(this))
    return set
}
