package ru.skillbox.socialnetwork.friend.common.client.fake;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountByFilterDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.account-service.enabled", havingValue = "false")
@Slf4j
public class FakeAccountServiceClient implements AccountServiceClient {

    private final Random random = new Random();

    @Override
    public AccountDto getAccountById(UUID id) {
        log.info("Запрос аккаунта по ID: {}", id);
        AccountDto account = generateRandomAccount();
        log.info("Сгенерирован фейковый аккаунт с ID: {}", account.getId());
        return account;
    }

    @Override
    public Page<AccountDto> getAccountsByFilter(AccountByFilterDto filter, Pageable pageable) {
        log.info("Получение списка аккаунтов по фильтру: {}", filter);
        List<AccountDto> accounts = new ArrayList<>();
        int count = 100;

        for (int i = 0; i < count; i++) {
            accounts.add(generateRandomAccount());
        }

        return Page.empty(pageable);
    }

    @Override
    public Page<AccountDto> findAccountsByIds(List<UUID> ids, Pageable pageable) {
        log.info("Поиск аккаунтов по списку ID (listSize: {}), page: {}, pageSize: {}, sort: {}",
                ids != null ? ids.size() : 0, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        List<AccountDto> accounts = new ArrayList<>();
        int count = 100;

        for (int i = 0; i < count; i++) {
            accounts.add(generateRandomAccount());
        }

        log.info("Сгенерирована страница с {} фейковыми аккаунтами по списку ID", accounts.size());
        return Page.empty(pageable);
    }

    @Override
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        List<AccountDto> accounts = new ArrayList<>();
        int count = 100;

        for (int i = 0; i < count; i++) {
            accounts.add(generateRandomAccount());
        }

        log.info("Сгенерирована страница с {} фейковыми аккаунтами по списку ID", accounts.size());
        return Page.empty(pageable);
    }

    @Override
    public AccountStatus getStatus(UUID userId) {
        log.info("Запрос статуса аккаунта для пользователя с ID: {}", userId);
        AccountStatus status = new AccountStatus(userId, false, false, false);
        log.info("Возвращён фейковый статус для пользователя {}: blocked={}, deleted={}, isNotFound={}",
                userId, status.isBlocked(), status.isDeleted(), status.isNotFound());
        return status;
    }

    public AccountDto generateRandomAccount() {
        AccountDto account = new AccountDto();
        int randomId = random.nextInt(1000) + 1;

        account.setId(UUID.randomUUID());
        account.setEmail("user" + randomId + "@example.com");
        account.setPhone("+7" + (9000000000L + random.nextInt(100000000)));
        account.setPhoto("https://example.com/photos/photo_" + randomId + ".jpg");
        account.setAbout("About user " + randomId);
        account.setCity("City_" + randomId);
        account.setCountry("Country_" + (random.nextInt(50) + 1));
        account.setFirstName("FirstName_" + randomId);
        account.setLastName("LastName_" + randomId);
        account.setRegDate(LocalDateTime.now().minusDays(random.nextInt(365)));
        account.setBirthDay(LocalDate.now().minusYears(random.nextInt(50) + 18).minusDays(random.nextInt(365)));
        account.setLastOnlineTime(LocalDateTime.now().minusMinutes(random.nextInt(1440)));
        account.setIsOnline(false);
        account.setIsBlocked(false);
        account.setIsDeleted(false);
        account.setPhotoName("profile_photo_" + randomId + ".png");
        account.setCreatedOn(LocalDateTime.now().minusDays(random.nextInt(365)));
        account.setUpdatedOn(LocalDateTime.now().minusDays(random.nextInt(30)));
        account.setEmojiStatus(random.nextBoolean() ? LocalDateTime.now().minusHours(random.nextInt(24)) : null);

        return account;
    }
}
