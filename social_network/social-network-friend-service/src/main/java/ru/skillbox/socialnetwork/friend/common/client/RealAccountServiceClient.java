package ru.skillbox.socialnetwork.friend.common.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountByFilterDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${feign.clients.account-service.name}", url = "${feign.clients.account-service.url}")
@ConditionalOnProperty(name = "app.account-service.enabled", havingValue = "true", matchIfMissing = true)
public interface RealAccountServiceClient extends AccountServiceClient {

    @GetMapping("/api/v1/account/{id}")
    AccountDto getAccountById(@PathVariable("id") UUID id);

    @PostMapping("/api/v1/account/searchByFilter")
    Page<AccountDto> getAccountsByFilter(@RequestBody AccountByFilterDto filter, Pageable pageable);

    @GetMapping("/api/v1/account/accountIds")
    Page<AccountDto> findAccountsByIds(@RequestParam List<UUID> ids, Pageable pageable);

    @GetMapping("/api/v1/account/status/{id}")
    AccountStatus getStatus(@PathVariable UUID id);

    // todo no-swagger
    @GetMapping("/api/v1/account")
    Page<AccountDto> getAllAccounts(Pageable pageable);

}