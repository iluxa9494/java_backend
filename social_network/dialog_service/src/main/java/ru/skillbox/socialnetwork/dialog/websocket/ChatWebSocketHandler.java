package ru.skillbox.socialnetwork.dialog.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.skillbox.socialnetwork.dialog.services.MessageService;

import java.io.IOException;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SessionRegistry sessionRegistry;
    private final MessageService messageService;

    public ChatWebSocketHandler(SessionRegistry sessionRegistry,
                                MessageService messageService) {
        this.sessionRegistry = sessionRegistry;
        this.messageService = messageService;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        sessionRegistry.addSession(userId, session);

        System.out.println("User connected: " + userId);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = objectMapper.readTree(message.getPayload());

        String type = json.get("type").asText();

        String recipientId = json.get("recipientId").asText();

        if ("MESSAGE".equals(type)) {
            sendToUser(recipientId, message.getPayload());
            messageService.saveMessage(json);
        }
    }


    private void sendToUser(String userId, String payload) throws IOException {
        WebSocketSession session = sessionRegistry.getSession(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(payload));
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");

        sessionRegistry.removeSession(userId);

        System.out.println("User disconnected: " + userId);
    }
}
