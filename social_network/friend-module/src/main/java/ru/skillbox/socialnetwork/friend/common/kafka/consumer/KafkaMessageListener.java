//package ru.skillbox.socialnetwork.friend.common.kafka.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import ru.skillbox.socialnetwork.friend.kafka.dto.KafkaMessage;
//import ru.skillbox.socialnetwork.friend.kafka.producer.KafkaMessageService;
//
//import java.util.UUID;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class KafkaMessageListener {
//
//    private final KafkaMessageService kafkaMessageService;
//
//    @KafkaListener(topics = "${app.kafka.kafkaMessageTopic}",
//    groupId = "${app.kafka.kafkaMessageGroupId}",
//    containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory")
//    public void listen(@Payload KafkaMessage message,
//                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID uuid,
//                       @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
//                       @Header(value = KafkaHeaders.RECEIVED_PARTITION) Integer partition,
//                       @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP, required = true) Long timestamp) {
//        log.info("Received message: {}", message);
//        log.info("UUID: {}, Partition: {}, Topic: {}, Timestamp: {}", uuid, partition, topic, timestamp);
//
//        kafkaMessageService.add(message);
//    }
//}
