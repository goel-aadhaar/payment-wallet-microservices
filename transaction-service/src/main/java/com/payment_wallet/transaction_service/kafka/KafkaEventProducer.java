package com.payment_wallet.transaction_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment_wallet.transaction_service.entity.Transaction;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendTransactionEvent(String key, String message) {
        System.out.println("Sending to kafka -> Topic: " + TOPIC + "Key: " + key + ", message: " + message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, message);

        future.thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();
            System.out.println("Kafka message sent successfully. Topic: " + metadata.topic() + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
        }).exceptionally(ex -> {
            System.err.println("Failed to send kafka message: " + ex.getMessage());
            return null;
        });
    }

    public void sendTransactionEvent(String key, Transaction transaction) {
        try {
            String message = objectMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing transaction: " + e.getMessage());
        }
    }
}
