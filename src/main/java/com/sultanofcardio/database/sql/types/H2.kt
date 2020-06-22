package com.sultanofcardio.database.sql.types

import com.sultanofcardio.database.sql.statement.*
import java.util.*

abstract class H2(name: String, connectionString: String, driverName: String): DatabaseType(name, connectionString, driverName) {
    override fun formatSelect(select: Select<*>): String {
        val result = StringBuilder(if (select.isDistinct) "SELECT DISTINCT " else "SELECT ")
        val columns = select.columns
        if (columns != null && columns.isNotEmpty()) {
            for (i in columns.indices) {
                val column = columns[i]
                if (i != columns.size - 1) {
                    result.append(String.format("%s, ", Statement.escape(column)))
                } else {
                    result.append(String.format("%s ", Statement.escape(column)))
                }
            }
        } else {
            result.append("* ")
        }
        val tableName = select.tableName
        return if (tableName != null && tableName.isNotEmpty()) {
            result.append(String.format("FROM %s ", tableName))
            val whereConditions = select.whereConditions
            var whereAppended = false
            if (whereConditions != null && whereConditions.isNotEmpty()) {
                result.append("WHERE ")
                whereAppended = true
                this.appendConditions(whereConditions, result)
            }
            val stringWhereConditions = select.stringWhereConditions
            if (stringWhereConditions != null && stringWhereConditions.isNotEmpty()) {
                if (!whereAppended) {
                    result.append("WHERE ")
                    whereAppended = true
                } else {
                    result.append("AND ")
                }
                this.appendConditions(stringWhereConditions, result)
            }
            H2Mem.appendGenericConditions(select, result, !whereAppended)
            val orderBy = select.orderBy
            if (orderBy != null && orderBy.size > 0) {
                result.append("ORDER BY ")
                for (i in orderBy.indices) {
                    val orderByCondition = orderBy[i] as String
                    result.append(orderByCondition)
                    if (i != orderBy.size - 1) {
                        result.append(", ")
                    }
                }
            }
            if (select.limit != -1) {
                result.append(String.format(" LIMIT %s ", select.limit))
            }
            result.toString().trim { it <= ' ' }
        } else {
            throw IllegalStateException("Table name not specified")
        }
    }

    override fun formatUpdate(update: Update<*>): String {
        val tableName = update.tableName
        return if (tableName != null && tableName.isNotEmpty()) {
            val result = StringBuilder(String.format("UPDATE %s SET ", tableName))
            val setConditions = update.setConditions
            val columnNameIterator: Iterator<String> = setConditions.keys.iterator()
            if (setConditions.isEmpty() && update.stringSetConditions.isEmpty()) {
                throw IllegalStateException("No column values found to modify")
            } else {
                var condition: String
                while (columnNameIterator.hasNext()) {
                    val columnName = columnNameIterator.next()
                    val value = setConditions[columnName]
                    condition = if (value == null || !String::class.java.isAssignableFrom(value.javaClass) && !Date::class.java.isAssignableFrom(value.javaClass)) "%s = %s" else "%s = '%s'"
                    if (columnNameIterator.hasNext()) {
                        result.append(String.format(String.format("%s, ", condition), columnName, Statement.escape(value)))
                    } else {
                        result.append(String.format(String.format("%s ", condition), columnName, Statement.escape(value)))
                    }
                }
                val stringSetConditions = update.stringSetConditions
                if (stringSetConditions.size > 0 && setConditions.isNotEmpty()) {
                    result.append(", ")
                }
                for (i in stringSetConditions.indices) {
                    condition = stringSetConditions[i] as String
                    if (i != stringSetConditions.size - 1) {
                        result.append(String.format("%s, ", condition))
                    } else {
                        result.append(String.format("%s ", condition))
                    }
                }
                val whereConditions = update.whereConditions
                var whereAppended = false
                if (whereConditions != null && whereConditions.isNotEmpty()) {
                    result.append("WHERE ")
                    whereAppended = true
                    this.appendConditions(whereConditions, result)
                }
                val stringWhereConditions = update.stringWhereConditions
                if (stringWhereConditions != null && stringWhereConditions.isNotEmpty()) {
                    if (!whereAppended) {
                        result.append("WHERE ")
                        whereAppended = true
                    } else {
                        result.append("AND ")
                    }
                    this.appendConditions(stringWhereConditions, result)
                }
                H2Mem.appendGenericConditions(update, result, whereAppended)
                result.toString().trim { it <= ' ' }
            }
        } else {
            throw IllegalStateException("Table name not specified")
        }
    }

    override fun formatInsert(insert: Insert<*>): String {
        val tableName = insert.tableName
        return if (tableName != null && tableName.isNotEmpty()) {
            val columnValues = insert.columnValues
            if (columnValues.isEmpty()) {
                throw IllegalStateException("No values found to be inserted")
            } else {
                val result = StringBuilder(String.format("INSERT INTO %s(", tableName))
                val columnNames = columnValues.keys.toTypedArray() as Array<String>
                val valuesString = StringBuilder("VALUES(")
                for (i in columnNames.indices) {
                    val columnName = columnNames[i]
                    if (i != columnNames.size - 1) {
                        result.append(String.format("%s, ", columnName))
                    } else {
                        result.append(String.format("%s) ", columnName))
                    }
                    val value = columnValues[columnNames[i]]
                    val formatString = if (value == null || !String::class.java.isAssignableFrom(value.javaClass) && !Date::class.java.isAssignableFrom(value.javaClass)) "%s" else "'%s'"
                    if (i != columnNames.size - 1) {
                        valuesString.append(String.format("%s, ", String.format(formatString, Statement.escape(value))))
                    } else {
                        valuesString.append(String.format("%s)", String.format(formatString, Statement.escape(value))))
                    }
                }
                result.append(valuesString).toString().trim { it <= ' ' }
            }
        } else {
            throw IllegalStateException("Table name not specified")
        }
    }

    override fun formatDelete(delete: Delete<*>): String {
        val tableName = delete.tableName
        return if (tableName != null && tableName.isNotEmpty()) {
            val result = StringBuilder(String.format("DELETE FROM %s ", tableName))
            val whereConditions = delete.whereConditions
            var whereAppended = false
            if (whereConditions != null && whereConditions.isNotEmpty()) {
                result.append("WHERE ")
                whereAppended = true
                this.appendConditions(whereConditions, result)
            }
            val stringWhereConditions = delete.stringWhereConditions
            if (stringWhereConditions != null && stringWhereConditions.isNotEmpty()) {
                if (!whereAppended) {
                    result.append("WHERE ")
                    whereAppended = true
                } else {
                    result.append("AND ")
                }
                this.appendConditions(stringWhereConditions, result)
            }
            H2Mem.appendGenericConditions(delete, result, !whereAppended)
            result.toString().trim { it <= ' ' }
        } else {
            throw IllegalStateException("Table name not specified")
        }
    }
}

object H2File: H2("H2", "jdbc:h2:./%s", "org.h2.Driver") {
    override fun getConnectionString(vararg args: String): String {
        require(args.isNotEmpty()) { "Must pass schema name" }
        return String.format(connectionString, args[0])
    }
}

object H2Mem: H2("H2", "jdbc:h2:mem:%s", "org.h2.Driver") {
    override fun getConnectionString(vararg args: String): String {
        require(args.isNotEmpty()) { "Must pass schema name" }
        return String.format(connectionString, args[0])
    }
}