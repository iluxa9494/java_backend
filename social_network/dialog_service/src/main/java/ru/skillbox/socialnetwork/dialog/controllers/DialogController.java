package ru.skillbox.socialnetwork.dialog.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.dialog.responses.*;
import ru.skillbox.socialnetwork.dialog.services.DialogService;
import ru.skillbox.socialnetwork.dialog.services.MessageService;

import java.util.List;
import java.util.UUID;

@RestController
public class DialogController {

    private final DialogService dialogService;
    private final MessageService messageService;

    public DialogController(DialogService dialogService, MessageService messageService) {
        this.dialogService = dialogService;
        this.messageService = messageService;
    }


    @PutMapping("/api/v1/dialogs/{dialogId}")
    public UpdateDialogRs updateDialog(@PathVariable UUID dialogId) {
        return dialogService.markDialogRead(dialogId);
    }


    @GetMapping("/api/v1/dialogs")
    public GetDialogsRs getDialogs(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        return dialogService.getDialogs(page, size, sort);
    }


    @GetMapping("/api/v1/dialogs/unread")
    public UnreadCountRs getUnread() {
        return dialogService.getUnreadCount();
    }


    @GetMapping("/api/v1/dialogs/recipientId/{recipientId}")
    public GetDialogRs getDialogByRecipientId(@PathVariable UUID recipientId) {
        return dialogService.getDialogByRecipientId(recipientId);
    }


    @GetMapping("/api/v1/dialogs/messages")
    public GetMessagesRs getMessages(
            @RequestParam UUID recipientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        return messageService.getMessages(recipientId, page, size, sort);
    }
}