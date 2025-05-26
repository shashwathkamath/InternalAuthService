package org.kamath.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.kamath.utils.Constants
import org.kamath.utils.logger
import org.jetbrains.exposed.sql.Database

/**
 * ACL db for verifying whether the user is allowed to access the station
 */
object DatabaseConfig {
    fun init(){
        try{
            val config = HikariConfig().apply {
                jdbcUrl = Constants.DB_URL
                driverClassName = Constants.DB_DRIVER_CLASSNAME
                username = Constants.DB_USERNAME
                password = Constants.DB_PASSWORD
                maximumPoolSize = Constants.DB_POOLSIZE
                isAutoCommit = false
                transactionIsolation = Constants.DB_TRANSACTION
            }
            Database.Companion.connect(HikariDataSource(config))
            logger.info("Successfully connected to DB on this URL ${Constants.DB_URL}")
        }
        catch (e: Exception){
            logger.error("Failed to connect with DB")
            throw RuntimeException("Database connection failed",e)
        }
    }
}