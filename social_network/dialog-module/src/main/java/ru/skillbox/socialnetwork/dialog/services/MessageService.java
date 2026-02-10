package ru.skillbox.socialnetwork.dialog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.dialog.config.security.SecurityService;
import ru.skillbox.socialnetwork.dialog.dtos.MessageDto;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;
import ru.skillbox.socialnetwork.dialog.exception.DialogNotFoundException;
import ru.skillbox.socialnetwork.dialog.model.Dialog;
import ru.skillbox.socialnetwork.dialog.model.Message;
import ru.skillbox.socialnetwork.dialog.repositories.DialogRepository;
import ru.skillbox.socialnetwork.dialog.repositories.MessageRepository;
import ru.skillbox.socialnetwork.dialog.responses.GetMessagesRs;
import ru.skillbox.socialnetwork.dialog.utils.SortUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final SecurityService securityService;


    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository, SecurityService securityService) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.securityService = securityService;
    }


    public GetMessagesRs getMessages(UUID recipientId, Integer page, Integer size, List<String> sort) {
        UUID currentUserId = securityService.getCurrentUserIdOrThrow();

        Sort sortSpec = SortUtils.parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<Message> messages = messageRepository
                .findAllByConversationPartners(currentUserId, recipientId, pageable);

        List<MessageDto> messageDtos = messages.stream().map(this::toDto).toList();

        return GetMessagesRs.builder()
                .error(null)
                .errorDescription(null)
                .timestamp((int) System.currentTimeMillis() / 1000)
                .total((int) messages.getTotalElements())
                .totalPages((int) messages.getTotalElements())
                .perPage(size)
                .offset(page)
                .unreadCount((int) messages.stream()
                    .filter(m -> m.getReadStatus() == ReadStatus.SENT)
                    .count())
                .content(messageDtos)
                .build();
    }


    public void saveMessage(JsonNode jsonMessage) {
        Message message = JsonToEntity(jsonMessage);
        Dialog dialog = dialogRepository
                .findById(message.getDialog().getId())
                .orElseThrow(() -> new DialogNotFoundException(message.getDialog().getId()));
        messageRepository.save(message);
        dialog.setLastMessage(message);
        dialogRepository.save(dialog);
    }


    public MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .dialogId(message.getDialog().getId())
                .time(message.getTime())
                .messageText(message.getMessageText())
                .conversationPartner1(message.getConversationPartner1())
                .conversationPartner2(message.getConversationPartner2())
                .readStatus(message.getReadStatus())
                .stubDate(message.getStubDate())
                .date(message.getDate())
                .build();
    }


    public Message toEntity(MessageDto dto) {
        Message message = new Message();
        message.setId(dto.getId());

        Dialog dialog = dialogRepository.findById(dto.getDialogId())
                .orElseThrow(() -> new IllegalArgumentException("Dialog not found with id: " + dto.getDialogId()));

        message.setDialog(dialog);
        message.setTime(dto.getTime());
        message.setMessageText(dto.getMessageText());
        message.setConversationPartner1(dto.getConversationPartner1());
        message.setConversationPartner2(dto.getConversationPartner2());
        message.setReadStatus(dto.getReadStatus());
        message.setStubDate(dto.getStubDate());
        message.setDate(dto.getDate());
        return message;
    }

    public Message JsonToEntity(JsonNode jsonMessage) {
        JsonNode data = jsonMessage.get("data");
        Instant instant = Instant.parse(data.get("time").asText());
        LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

        UUID partner1 = UUID.fromString(data.get("conversationPartner1").asText());
        UUID partner2 = UUID.fromString(data.get("conversationPartner2").asText());
        UUID dialogId = UUID.fromString(data.get("dialogId").asText());

        String messageText = data.get("messageText").asText();

        String readStatusStr = data.get("readStatus").asText();
        ReadStatus readStatus = ReadStatus.SENT;

        Message message = new Message();
        message.setDialog(dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(dialogId)));
        message.setTime(time);
        message.setConversationPartner1(partner1);
        message.setConversationPartner2(partner2);
        message.setMessageText(messageText);
        message.setReadStatus(readStatus);
        return message;
    }
}
