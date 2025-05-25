package org.kamath.utils

object Constants {
    //QueueName
    const val AUTH_REQUEST_QUEUE= "auth-requests"
    const val AUTH_RESPONSE_QUEUE = "auth-response"

    //Rabbit MQ credentials
    const val RABBITMQ_HOST = "localhost"
    const val RABBITMQ_PORT = 5672
    const val RABBITMQ_USERNAME = "guest"
    const val RABBITMQ_PASSWORD = "guest"

    //RABBIT MQ Messages
    const val CONSUMER_STARTED = "Consumer Started"
    const val MESSAGE_RECEIVED = "Message Received"
    const val MESSAGE_SENT = "Message Sent"
    const val CONSUMER_FAILED = "Consumer Failed to start"

    //DB Config
    const val DB_URL = "jdbc:postgresql://localhost:5432/acl_db"
    const val DB_DRIVER_CLASSNAME = "org.postgresql.Driver"
    const val DB_USERNAME = "postgres"
    const val DB_PASSWORD = "test@123"
    const val DB_POOLSIZE = 10
    const val DB_TRANSACTION = "TRANSACTION_REPEATABLE_READ"
}