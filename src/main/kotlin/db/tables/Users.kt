package org.kamath.db.tables

import org.jetbrains.exposed.sql.Table

object Users: Table("users") {
    val id = integer("id").autoIncrement()
    val stationId = varchar("station_id",100)
    val driverToken = varchar("driver_token",255)
    val callbackUrl = varchar("callback_url",255)

    override val primaryKey = PrimaryKey(id)
}