package org.kamath.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object DriverAuthTable: Table("driver_auth") {
    val id = uuid("id").autoGenerate()
    val username = varchar("username",50).uniqueIndex()
    val password = varchar("password",64)
    val driverToken = varchar("driver_token",128).uniqueIndex()
    val createdAt = datetime("create_at")
}