package ru.skillbox.socialnetwork.friend.common.client.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.account.dto.AccountByFilterDto;
import ru.skillbox.socialnetwork.account.dto.AccountDto;
import ru.skillbox.socialnetwork.account.dto.AccountSearchDto;
import ru.skillbox.socialnetwork.account.dto.AccountStatus;
import ru.skillbox.socialnetwork.account.service.AccountService;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;

import java.util.List;
import java.util.UUID;

@Component
@Primary
@ConditionalOnProperty(name = "app.account-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class InternalAccountServiceClient implements AccountServiceClient {

    private final AccountService accountService;

    @Override
    public ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto getAccountById(UUID id) {
        return mapAccount(accountService.getAccountById(id));
    }

    @Override
    public Page<ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto> getAccountsByFilter(
            ru.skillbox.socialnetwork.friend.friend.dto.account.AccountByFilterDto filter,
            Pageable pageable) {
        AccountByFilterDto mappedFilter = mapFilter(filter, pageable);
        return accountService.searchByFilter(mappedFilter).map(this::mapAccount);
    }

    @Override
    public Page<ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto> findAccountsByIds(
            List<UUID> ids,
            Pageable pageable) {
        return accountService.getAccountsByIds(ids, pageable).map(this::mapAccount);
    }

    @Override
    public Page<ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto> getAllAccounts(Pageable pageable) {
        return accountService.getAll(pageable).map(this::mapAccount);
    }

    @Override
    public ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus getStatus(UUID userId) {
        AccountStatus status = accountService.getAccountStatus(userId);
        return new ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus(
                status.id(),
                status.isDeleted(),
                status.isBlocked(),
                status.isNotFound()
        );
    }

    private AccountByFilterDto mapFilter(
            ru.skillbox.socialnetwork.friend.friend.dto.account.AccountByFilterDto filter,
            Pageable pageable) {
        AccountByFilterDto mapped = new AccountByFilterDto();
        ru.skillbox.socialnetwork.friend.friend.dto.account.AccountSearchDto searchDto =
                filter != null ? filter.getAccountSearchDto() : null;
        AccountSearchDto mappedSearch = null;

        if (searchDto != null) {
            mappedSearch = new AccountSearchDto();
            mappedSearch.setIds(searchDto.getIds());
            mappedSearch.setAuthor(searchDto.getAuthor());
            mappedSearch.setFirstName(searchDto.getFirstName());
            mappedSearch.setLastName(searchDto.getLastName());
            mappedSearch.setBirthDateFrom(searchDto.getBirthDateFrom());
            mappedSearch.setBirthDateTo(searchDto.getBirthDateTo());
            mappedSearch.setCity(searchDto.getCity());
            mappedSearch.setCountry(searchDto.getCountry());
            mappedSearch.setIsBlocked(searchDto.getIsBlocked());
            mappedSearch.setIsDeleted(searchDto.getIsDeleted());
            mappedSearch.setAgeFrom(searchDto.getAgeFrom());
            mappedSearch.setAgeTo(searchDto.getAgeTo());
        }

        mapped.setAccountSearchDto(mappedSearch);

        Integer pageNumber = filter != null ? filter.getPageNumber() : null;
        Integer pageSize = filter != null ? filter.getPageSize() : null;
        if (pageNumber == null && pageable != null) {
            pageNumber = pageable.getPageNumber();
        }
        if (pageSize == null && pageable != null) {
            pageSize = pageable.getPageSize();
        }
        mapped.setPageNumber(pageNumber);
        mapped.setPageSize(pageSize);
        return mapped;
    }

    private ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto mapAccount(AccountDto account) {
        ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto dto =
                new ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setPhoto(account.getPhoto());
        dto.setAbout(account.getAbout());
        dto.setCity(account.getCity());
        dto.setCountry(account.getCountry());
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setRegDate(account.getRegDate());
        dto.setBirthDay(account.getBirthDate());
        dto.setLastOnlineTime(account.getLastOnlineTime());
        dto.setIsOnline(account.isOnline());
        dto.setIsBlocked(account.getIsBlocked());
        dto.setIsDeleted(account.getIsDeleted());
        dto.setPhotoName(account.getPhotoName());
        dto.setCreatedOn(account.getCreatedOn());
        dto.setUpdatedOn(account.getUpdatedOn());
        dto.setEmojiStatus(null);
        return dto;
    }
}
