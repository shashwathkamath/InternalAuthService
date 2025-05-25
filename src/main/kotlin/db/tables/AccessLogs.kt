package org.kamath.db.tables

import org.jetbrains.exposed.sql.javatime.datetime

import org.jetbrains.exposed.sql.Table

object AccessLogs: Table("access_logs") {
    val id = integer("id").autoIncrement()
    val stationId = varchar("station_id",100)
    val driverToken = varchar("driver_token",255)
    val status = varchar("status",50)
    val timestamp = datetime("timestamp")

    override val primaryKey = PrimaryKey(id)
}
