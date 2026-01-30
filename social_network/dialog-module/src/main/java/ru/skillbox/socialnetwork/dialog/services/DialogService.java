package ru.skillbox.socialnetwork.dialog.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.dialog.config.security.SecurityService;
import ru.skillbox.socialnetwork.dialog.dtos.DialogDto;
import ru.skillbox.socialnetwork.dialog.dtos.MessageDto;
import ru.skillbox.socialnetwork.dialog.dtos.UnreadCountDto;
import ru.skillbox.socialnetwork.dialog.dtos.UpdateDialogDto;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;
import ru.skillbox.socialnetwork.dialog.model.Dialog;
import ru.skillbox.socialnetwork.dialog.model.Message;
import ru.skillbox.socialnetwork.dialog.repositories.DialogRepository;
import ru.skillbox.socialnetwork.dialog.repositories.MessageRepository;
import ru.skillbox.socialnetwork.dialog.responses.GetDialogRs;
import ru.skillbox.socialnetwork.dialog.responses.GetDialogsRs;
import ru.skillbox.socialnetwork.dialog.responses.UnreadCountRs;
import ru.skillbox.socialnetwork.dialog.responses.UpdateDialogRs;
import ru.skillbox.socialnetwork.dialog.exception.DialogAccessDeniedException;
import ru.skillbox.socialnetwork.dialog.exception.DialogNotFoundException;
import ru.skillbox.socialnetwork.dialog.utils.SortUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DialogService {

    private final SecurityService securityService;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    public DialogService(DialogRepository dialogRepository, MessageService messageService, SecurityService securityService, MessageRepository messageRepository) {
        this.dialogRepository = dialogRepository;
        this.messageService = messageService;
        this.securityService = securityService;
        this.messageRepository = messageRepository;
    }


    public UpdateDialogRs markDialogRead(UUID dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(dialogId));
        UUID currentUserId = securityService.getCurrentUserIdOrThrow();

        if (dialog.getLastMessage() != null && dialog.getLastMessage().getConversationPartner1().equals(currentUserId)) {
            return UpdateDialogRs.builder()
                    .data(new UpdateDialogDto("No messages to mark as read"))
                    .error(null)
                    .timestamp((int) System.currentTimeMillis() / 1000)
                    .errorDescription(null)
                    .build();
        }

        if (!dialog.getConversationPartner1().equals(currentUserId) &&
            !dialog.getConversationPartner2().equals(currentUserId)) {
            throw new DialogAccessDeniedException(dialogId, currentUserId);
        }

        List<Message> messagesToBeMarkedRead = messageRepository.findMessagesByDialogIdAndStatus(dialogId, ReadStatus.SENT);
        messagesToBeMarkedRead.forEach(message -> message.setReadStatus(ReadStatus.READ));
        if (!messagesToBeMarkedRead.isEmpty()) {
            messagesToBeMarkedRead.forEach(message -> message.setReadStatus(ReadStatus.READ));
            messageRepository.saveAll(messagesToBeMarkedRead);
            dialog.setUnreadCount(0);
            dialogRepository.save(dialog);
        }

        UpdateDialogDto updateDialogDto = new UpdateDialogDto();
        updateDialogDto.setMessage("Messages marked as read");

        return UpdateDialogRs.builder()
                .data(updateDialogDto)
                .error(null)
                .timestamp((int) System.currentTimeMillis() / 1000)
                .errorDescription(null)
                .build();
    }


    public GetDialogsRs getDialogs(Integer page, Integer size, List<String> sort) {
        UUID currentUserId = securityService.getCurrentUserIdOrThrow();

        Sort sortSpec = SortUtils.parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        Page<Dialog> dialogsPage = dialogRepository.findAllByUserId(currentUserId, pageable);

        List<DialogDto> dialogDtos = dialogsPage.stream().map(this::toDto).toList();

        return GetDialogsRs.builder()
                .error(null)
                .errorDescription(null)
                .timestamp((int) System.currentTimeMillis() / 1000)
                .total((int) dialogsPage.getTotalElements())
                .offset(page)
                .perPage(size)
                .id(UUID.randomUUID())
                .content(dialogDtos)
                .build();
    }


    public UnreadCountRs getUnreadCount() {
        UUID currentUserId = securityService.getCurrentUserIdOrThrow();
        long countLong = dialogRepository.countDialogsByUserIdAndUnreadCountGreaterThan(currentUserId, 0);

        int count = countLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) countLong;

        UnreadCountDto data = new UnreadCountDto();
        data.setCount(count);

        return UnreadCountRs.builder()
                .error(null)
                .timestamp((int) System.currentTimeMillis() / 1000)
                .data(data)
                .errorDescription(null)
                .build();
    }


    public GetDialogRs getDialogByRecipientId(UUID recipientId) {
        UUID currentUserId = securityService.getCurrentUserIdOrThrow();

        Dialog dialog = dialogRepository.findByConversationPartners(currentUserId, recipientId)
                .orElseGet(() -> {
                    log.info("Dialog between users {} and {} not found. Creating new one.", currentUserId, recipientId);
                    Dialog newDialog = new Dialog();
                    newDialog.setConversationPartner1(currentUserId);
                    newDialog.setConversationPartner2(recipientId);
                    newDialog.setUnreadCount(0);
                    return dialogRepository.save(newDialog);
                });

        DialogDto dialogDto = toDto(dialog);

        return GetDialogRs.builder()
                .id(dialogDto.getId())
                .unreadCount(dialogDto.getUnreadCount())
                .lastMessage(dialogDto.getLastMessage())
                .conversationPartner1(dialogDto.getConversationPartner1())
                .conversationPartner2(dialogDto.getConversationPartner2())
                .build();
    }


    public DialogDto toDto(Dialog dialog) {
        MessageDto messageDto = null;
        if (dialog.getLastMessage() != null) {
            messageDto = messageService.toDto(dialog.getLastMessage());
        }

        return DialogDto.builder()
                .lastMessage(messageDto)
                .id(dialog.getId())
                .unreadCount(dialog.getUnreadCount())
                .conversationPartner1(dialog.getConversationPartner1())
                .conversationPartner2(dialog.getConversationPartner2())
                .build();
    }


    public Dialog toEntity(DialogDto dto) {
        Dialog dialog = new Dialog();
        dialog.setId(dto.getId());
        dialog.setUnreadCount(dto.getUnreadCount());
        dialog.setConversationPartner1(dto.getConversationPartner1());
        dialog.setConversationPartner2(dto.getConversationPartner2());
        dialog.setLastMessage(messageService.toEntity(dto.getLastMessage()));
        return dialog;
    }
}