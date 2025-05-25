package org.kamath.db.seed

import org.kamath.db.tables.AccessLogs
import org.kamath.db.tables.AccessRules
import org.kamath.db.tables.Users
import org.kamath.utils.logger
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object SeedHelper {

    fun seedDummyUsers(){
        try {
            transaction {
                SchemaUtils.create(Users, AccessRules, AccessLogs)

                val dummyData = listOf(
                    Triple("b7b8d74e-542d-4783-a084-bbdc3d3bc1f4", "Abc_1234-Token-valid.333", "http://localhost:8080/callback"),
                    Triple("c8c9e77f-1234-4321-bbaa-ffccddeeffaa", "Token-XYZ-789", "http://localhost:8080/callback"),
                    Triple("d0d1e82a-9876-5678-abcd-112233445566", "Driver-Token-456", "http://localhost:8080/callback"),
                    Triple("e1e2f34a-0000-1111-aaaa-bbccddeeff00", "Invalid-Token-000", "http://localhost:8080/callback"),
                    Triple("f2f3a45b-2222-3333-bbbb-aabbccddeeff", "Unknown-Driver-999", "http://localhost:8080/callback")
                )

                val statusMap = mapOf(
                    "Abc_1234-Token-valid.333" to "allowed",
                    "Token-XYZ-789" to "not_allowed",
                    "Driver-Token-456" to "allowed",
                    "Invalid-Token-000" to "invalid",
                    "Unknown-Driver-999" to "unknown"
                )

                dummyData.forEach { (id,token,url) ->
                    Users.insert {
                        it[Users.stationId] = id
                        it[Users.driverToken] = token
                        it[Users.callbackUrl] = url
                    }
                    AccessRules.insert {
                        it[AccessRules.stationId] = id
                        it[AccessRules.driverToken] = token
                        it[AccessRules.status] = statusMap[token]?:"unkown"
                    }
                }
                logger.info("Seed Data Inserted Successfully")
            }
        }
        catch (e: Exception){
            logger.error("Issue with data insertion $e")
            throw RuntimeException("Issue with seed data insertion in db",e)
        }
    }
}