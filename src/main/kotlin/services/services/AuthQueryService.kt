package org.kamath.services

import com.rabbitmq.client.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.models.AuthRequest
import org.example.models.AuthResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kamath.db.tables.AccessLogs
import org.kamath.utils.Constants
import org.kamath.utils.logger
import java.time.LocalDateTime

object AuthQueryService: IAuthQueryService {

    private val factory: ConnectionFactory by lazy {
        ConnectionFactory().apply {
            host = Constants.RABBITMQ_HOST
            port = Constants.RABBITMQ_PORT
            username = Constants.RABBITMQ_USERNAME
            password = Constants.RABBITMQ_PASSWORD
        }
    }

    private val connection: Connection by lazy {
        factory.newConnection()
    }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val httpClient = HttpClient{
        install(ContentNegotiation){
            json()
        }
        install(HttpTimeout){
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
    }


    override suspend fun sendToQueue(authRequest: AuthRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun startConsumer(): String? = withContext(Dispatchers.IO){
        val queueName = Constants.AUTH_REQUEST_QUEUE
        val channel = connection.createChannel()

        channel.queueDeclare(queueName,true,false,false,null)
        logger.info("Queue declaration done!")

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {
                val message = body?.toString(Charsets.UTF_8) ?: return
                logger.info("Message received from rabbitmq $message")
                val request = Json.decodeFromString<AuthRequest>(message)

                scope.launch{
                    try {
                        val response = httpClient.post("http://localhost:8081/verify") {
                            contentType(ContentType.Application.Json)
                            setBody(request)
                            timeout {
                                requestTimeoutMillis = 3000
                            }
                        }
                        response.body<AuthResponse>()
                    }
                    catch (e: Exception){
                        logger.error("Timeout or error connecting internal api",e)
                    }
                }
            }
        }
        channel.basicConsume(queueName,true,consumer)
    }

    private suspend fun sendCallback(callbackUrl: String, payload: AuthResponse){
        try {
            val response = httpClient.post(callbackUrl) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
            if (response.status.isSuccess()){
                val insertResult = transaction {
                    AccessLogs.insert {
                        it[stationId] = payload.stationId
                        it[driverToken] = payload.driverToken
                        it[status] = payload.status.name
                        it[timestamp] = LocalDateTime.now()
                    }
                }
                if (insertResult!=null){
                    logger.info("Access Log table updated with latest decision ${insertResult.insertedCount}")
                }
                else{
                    logger.error("Issue while updating latest access log")
                }
                logger.info("Response sent to callback url successfully ${response.status}")
            }
            else{
                logger.error("Issue sending response to callback url ${response.status}")
            }
        }
        catch (e: Exception){
            logger.error("Error sending callback $e")
        }
    }
}