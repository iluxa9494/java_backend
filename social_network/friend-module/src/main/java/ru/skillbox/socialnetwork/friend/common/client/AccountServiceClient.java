package ru.skillbox.socialnetwork.friend.common.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountByFilterDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus;

import java.util.List;
import java.util.UUID;

public interface AccountServiceClient {

    AccountDto getAccountById(UUID id);

    Page<AccountDto> getAccountsByFilter(AccountByFilterDto filter, Pageable pageable);

    Page<AccountDto> findAccountsByIds(List<UUID> ids, Pageable pageable);

    Page<AccountDto> getAllAccounts(Pageable pageable);

    AccountStatus getStatus(UUID userId);
}
