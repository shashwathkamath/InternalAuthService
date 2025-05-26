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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.models.AccessStatus
import org.example.models.AuthCallbackPayload
import org.example.models.AuthRequest
import org.example.models.AuthResponse
import org.kamath.utils.Constants
import org.kamath.utils.logger

/**
 * The auth service will start by consuming the request from ChargingSessionService
 * The auth service will call /verify api to access and verify db
 */
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

    override suspend fun sendToQueue(authCallbackPayload: AuthCallbackPayload) {
        val queueName = Constants.AUTH_RESPONSE_QUEUE
        val channel = connection.createChannel()

        channel.queueDeclare(queueName,true,false,false,null)
        val message = Json.encodeToString<AuthCallbackPayload>(authCallbackPayload)
        channel.basicPublish("", queueName, null, message.toByteArray())

        logger.info("Message received from internal api $message")
    }

    override suspend fun startConsumer(): String? = withContext(Dispatchers.IO){
        val queueName = Constants.AUTH_REQUEST_QUEUE
        val channel = connection.createChannel()

        channel.queueDeclare(queueName,true,false,false,null)
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
                        if (response.status.isSuccess()){
                            response.body<AuthResponse>()
                        }
                        else{
                            logger.warn("Auth service returned ${response.status}")
                            AuthResponse(request.stationId,
                                request.driverToken,
                                AccessStatus.unknown)
                        }
                    }
                    catch (e: Exception){
                        logger.error("Timeout or error connecting internal api",e)
                    }
                }
            }
        }
        channel.basicConsume(queueName,true,consumer)
    }
}