package org.kamath.db.tables

import org.jetbrains.exposed.sql.Table

object AccessRules: Table("access_rules") {
    val id = uuid("id").autoGenerate().uniqueIndex()
    val stationId = varchar("station_id",100)
    val driverToken = varchar("driver_token",255)
    val status = varchar("status",50)

    override val primaryKey = PrimaryKey(id)
}