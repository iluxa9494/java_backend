package ru.skillbox.socialnetwork.dialog.services;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.skillbox.socialnetwork.dialog.config.security.SecurityService;
import ru.skillbox.socialnetwork.dialog.repositories.DialogRepository;
import ru.skillbox.socialnetwork.dialog.repositories.MessageRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Test
    void getMessages_usesTimeAscendingByDefault() {
        MessageRepository messageRepository = mock(MessageRepository.class);
        DialogRepository dialogRepository = mock(DialogRepository.class);
        SecurityService securityService = mock(SecurityService.class);

        UUID currentUserId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        when(securityService.getCurrentUserIdOrThrow()).thenReturn(currentUserId);
        when(messageRepository.findAllByConversationPartners(eq(currentUserId), eq(recipientId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        MessageService service = new MessageService(messageRepository, dialogRepository, securityService);

        service.getMessages(recipientId, 0, 10, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(messageRepository).findAllByConversationPartners(eq(currentUserId), eq(recipientId), pageableCaptor.capture());

        Sort sort = pageableCaptor.getValue().getSort();
        assertThat(sort.getOrderFor("time")).isNotNull();
        assertThat(sort.getOrderFor("time").getDirection()).isEqualTo(Sort.Direction.ASC);
    }
}
