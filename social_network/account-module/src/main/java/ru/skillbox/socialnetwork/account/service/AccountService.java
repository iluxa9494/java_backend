package ru.skillbox.socialnetwork.account.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.account.authentication.events.NewUserRegisteredEvent;
import ru.skillbox.socialnetwork.integration.service.StorageService;
import ru.skillbox.socialnetwork.account.dto.AccountByFilterDto;
import ru.skillbox.socialnetwork.account.dto.AccountDto;
import ru.skillbox.socialnetwork.account.dto.AccountSearchDto;
import ru.skillbox.socialnetwork.account.dto.AccountStatus;
import ru.skillbox.socialnetwork.account.entity.AccountEntity;
import ru.skillbox.socialnetwork.account.repository.AccountRepository;
import ru.skillbox.socialnetwork.account.repository.AccountSpecifications;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final StorageService storageService;
    private final AccountValidator accountValidator;
    private final EventPublisher eventPublisher;

    @Transactional(transactionManager = "accountTransactionManager")
    public void createAccountFromEvent(NewUserRegisteredEvent event) {
        log.info("📩 Создаём аккаунт из события: {}", event.getEmail());

        if (accountRepository.findByEmail(event.getEmail()).isPresent()) {
            log.warn("⚠️ Аккаунт с email {} уже существует", event.getEmail());
            return;
        }

        AccountEntity account = AccountEntity.builder()
                .id(event.getId())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .isBlocked(false)
                .isDeleted(false)
                .regDate(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        log.info("✅ Аккаунт сохранён: id={}, email={}", account.getId(), account.getEmail());
    }

    private void checkAccountAvailability(AccountEntity account) {
        if (Boolean.TRUE.equals(account.getIsDeleted())) {
            log.warn("🚫 Попытка операции с удаленным аккаунтом: {}", account.getId());
            throw new RuntimeException("Account is deleted");
        }
        if (Boolean.TRUE.equals(account.getIsBlocked())) {
            log.warn("🚫 Попытка операции с заблокированным аккаунтом: {}", account.getId());
            throw new RuntimeException("Account is blocked");
        }
    }

    public Page<AccountDto> getAccountsByIds(List<UUID> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }

        List<AccountEntity> entities = accountRepository.findByIdIn(ids);

        List<AccountDto> accounts = entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        List<AccountDto> filteredAccounts = accounts.stream()
                .filter(dto -> ids.contains(dto.getId()))
                .collect(Collectors.toList());

        int start = (int) Math.min(pageable.getOffset(), filteredAccounts.size());
        int end = Math.min(start + pageable.getPageSize(), filteredAccounts.size());

        List<AccountDto> pagedContent = filteredAccounts.subList(start, end);

        return new PageImpl<>(pagedContent, pageable, filteredAccounts.size());
    }

    @Cacheable(value = "account", key = "#id")
    public AccountDto getAccountById(UUID id) {
        log.info("🔍 Загрузка аккаунта из БД: {}", id);
        AccountEntity entity = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден: {}", id);
                    return new RuntimeException("Account not found");
                });
        log.debug("✅ Аккаунт загружен: {} {}", entity.getFirstName(), entity.getLastName());
        return mapToDto(entity);
    }

    public AccountDto createAccount(AccountDto dto) {
        log.info("🆕 Создание нового аккаунта: {}", dto.getEmail());
        accountValidator.validateCreate(dto);

        AccountEntity entity = new AccountEntity();
        mapToEntity(dto, entity);

        entity.setRegDate(LocalDateTime.now());
        entity.setCreatedOn(LocalDateTime.now());
        entity.setUpdatedOn(LocalDateTime.now());
        entity.setIsBlocked(false);
        entity.setIsDeleted(false);

        AccountEntity saved = accountRepository.save(entity);
        log.info("✅ Аккаунт создан успешно: id={}, email={}", saved.getId(), saved.getEmail());
        return mapToDto(saved);
    }

    @CacheEvict(cacheNames = {"account", "account_status"}, key = "#id")
    public AccountDto updateCurrentAccount(UUID id, AccountDto dto) {
        log.info("✏️ Обновление аккаунта: {}", id);

        AccountEntity existing = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден для обновления: {}", id);
                    return new RuntimeException("Account not found");
                });

        if (dto.getPhoto() == null || dto.getPhoto().isEmpty()) {
            this.deleteUserPhoto(existing);
        }

        checkAccountAvailability(existing);
        accountValidator.validateUpdate(dto, existing.getEmail());

        mapToEntity(dto, existing);

        existing.setUpdatedOn(LocalDateTime.now());
        AccountEntity updated = accountRepository.save(existing);
        return mapToDto(updated);
    }

    @CacheEvict(cacheNames = {"account", "account_status"}, key = "#id")
    public void deleteAccount(UUID id) {
        log.info("🗑️ Удаление аккаунта: {}", id);

        AccountEntity entity = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден для удаления: {}", id);
                    return new RuntimeException("Account not found");
                });
        this.deleteUserPhoto(entity);
        accountRepository.save(entity);
        log.info("✅ Аккаунт помечен как удаленный: {}", id);
    }

    private void deleteUserPhoto(AccountEntity entity) {
        if (entity == null) {
            log.debug("Пользователя не существует");
            return;
        }
        String photoPath = entity.getPhoto();
        if (photoPath == null) {
            log.debug("У пользователя нет фото для удаления");
            return;
        }

        if (!photoPath.isEmpty()) {
            try {
                log.debug("Удаление фото пользователя: {}", photoPath);
                storageService.deleteImage(photoPath);
                log.info("Фото успешно удалено: {}", photoPath);
            } catch (Exception e) {
                log.error("Непредвиденная ошибка при удалении фото: {}", e.getMessage(), e);
            }
        } else {
            log.debug("У пользователя нет фото для удаления");
        }
    }

    public Page<AccountDto> getAll(Pageable pageable) {
        Page<AccountEntity> entities = accountRepository.findAll(pageable);
        return entities.map(this::mapToDto);
    }

    @CacheEvict(cacheNames = {"account", "account_status"}, key = "#id")
    public void blockAccount(UUID id) {
        log.info("🚫 Блокировка аккаунта: {}", id);

        AccountEntity entity = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден для блокировки: {}", id);
                    return new RuntimeException("Account not found");
                });

        entity.setIsBlocked(true);
        entity.setUpdatedOn(LocalDateTime.now());
        accountRepository.save(entity);
        log.info("✅ Аккаунт заблокирован: {}", id);
    }

    @CacheEvict(cacheNames = {"account", "account_status"}, key = "#id")
    public void unblockAccount(UUID id) {
        log.info("✅ Разблокировка аккаунта: {}", id);

        AccountEntity entity = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден для разблокировки: {}", id);
                    return new RuntimeException("Account not found");
                });

        entity.setIsBlocked(false);
        entity.setUpdatedOn(LocalDateTime.now());
        accountRepository.save(entity);
        log.info("✅ Аккаунт разблокирован: {}", id);
    }

    public Page<AccountDto> searchByFilter(AccountByFilterDto filterDto) {
        AccountSearchDto searchDto = filterDto.getAccountSearchDto();
        Pageable pageable = PageRequest.of(
                filterDto.getPageNumber() != null ? filterDto.getPageNumber() : 0,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10
        );

        log.info("🔍 [SERVICE] searchByFilter - Начало поиска по фильтру");
        log.info("📋 [SERVICE] Параметры поиска: {}", searchDto);

        Specification<AccountEntity> spec = Specification.allOf();

        if (searchDto.getIds() != null && !searchDto.getIds().isEmpty()) {
            spec = spec.and(AccountSpecifications.withIds(searchDto.getIds()));
        }
        if (searchDto.getAuthor() != null) {
            spec = spec.and(AccountSpecifications.withAuthor(searchDto.getAuthor()));
        }
        if (searchDto.getFirstName() != null) {
            spec = spec.and(AccountSpecifications.withFirstName(searchDto.getFirstName()));
        }
        if (searchDto.getLastName() != null) {
            spec = spec.and(AccountSpecifications.withLastName(searchDto.getLastName()));
        }
        if (searchDto.getCity() != null) {
            spec = spec.and(AccountSpecifications.withCity(searchDto.getCity()));
        }
        if (searchDto.getCountry() != null) {
            spec = spec.and(AccountSpecifications.withCountry(searchDto.getCountry()));
        }
        if (searchDto.getIsBlocked() != null) {
            spec = spec.and(AccountSpecifications.withIsBlocked(searchDto.getIsBlocked()));
        }
        if (searchDto.getIsDeleted() != null) {
            spec = spec.and(AccountSpecifications.withIsDeleted(searchDto.getIsDeleted()));
        }
        if (searchDto.getBirthDateFrom() != null) {
            spec = spec.and(AccountSpecifications.withBirthDateFrom(searchDto.getBirthDateFrom()));
        }
        if (searchDto.getBirthDateTo() != null) {
            spec = spec.and(AccountSpecifications.withBirthDateTo(searchDto.getBirthDateTo()));
        }
        if (searchDto.getAgeFrom() != null) {
            spec = spec.and(AccountSpecifications.withAgeFrom(searchDto.getAgeFrom()));
        }
        if (searchDto.getAgeTo() != null) {
            spec = spec.and(AccountSpecifications.withAgeTo(searchDto.getAgeTo()));
        }

        Page<AccountEntity> result = accountRepository.findAll(spec, pageable);
        log.info("✅ Найдено {} аккаунтов по фильтру", result.getTotalElements());
        return result.map(this::mapToDto);
    }

    public List<AccountDto> find(List<UUID> ids) {
        List<AccountEntity> entities = accountRepository.findAllById(ids);
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AccountDto> search(String query) {
        List<AccountEntity> entities = accountRepository.findByQuery("%" + query.toLowerCase() + "%");
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<String> getAccountIds() {
        return accountRepository.findAllIds()
                .stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    public AccountStatus getAccountStatus(UUID userId) {
        log.info("Получение статуса аккаунта для userId: {}", userId);

        AccountEntity account = accountRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("❌ Аккаунт не найден: {}", userId);
                    return new RuntimeException("Account not found");
                });

        AccountStatus status = new AccountStatus(
                account.getId(),
                Boolean.TRUE.equals(account.getIsDeleted()),
                Boolean.TRUE.equals(account.getIsBlocked()),
                false
        );

        log.info("Статус аккаунта: id={}, deleted={}, blocked={}, notFound={}",
                status.id(), status.isDeleted(), status.isBlocked(), status.isNotFound());
        return status;
    }

    public Page<AccountDto> searchWithPagination(
            List<UUID> ids, String author, String firstName, String lastName,
            String city, String country, Boolean isBlocked, Boolean isDeleted,
            Integer ageTo, Integer ageFrom, Pageable pageable) {

        log.info("🔍 [SERVICE] searchWithPagination - Поиск с пагинацией");

        Specification<AccountEntity> spec = Specification.allOf();

        if (ids != null && !ids.isEmpty()) {
            spec = spec.and(AccountSpecifications.withIds(ids));
        }
        if (author != null) {
            spec = spec.and(AccountSpecifications.withAuthor(author));
        }
        if (firstName != null) {
            spec = spec.and(AccountSpecifications.withFirstName(firstName));
        }
        if (lastName != null) {
            spec = spec.and(AccountSpecifications.withLastName(lastName));
        }
        if (city != null) {
            spec = spec.and(AccountSpecifications.withCity(city));
        }
        if (country != null) {
            spec = spec.and(AccountSpecifications.withCountry(country));
        }
        if (isBlocked != null) {
            spec = spec.and(AccountSpecifications.withIsBlocked(isBlocked));
        }
        if (isDeleted != null) {
            spec = spec.and(AccountSpecifications.withIsDeleted(isDeleted));
        }
        if (ageFrom != null) {
            spec = spec.and(AccountSpecifications.withAgeFrom(ageFrom));
        }
        if (ageTo != null) {
            spec = spec.and(AccountSpecifications.withAgeTo(ageTo));
        }

        Page<AccountEntity> result = accountRepository.findAll(spec, pageable);
        log.info("✅ Найдено {} аккаунтов", result.getTotalElements());
        return result.map(this::mapToDto);
    }

    private AccountDto mapToDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setPhoto(entity.getPhoto());
        dto.setAbout(entity.getAbout());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setRegDate(entity.getRegDate());
        dto.setLastOnlineTime(entity.getLastOnlineTime());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setIsDeleted(entity.getIsDeleted());
        dto.setPhotoName(entity.getPhotoName());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());
        dto.setEmojiStatus(entity.getEmojiStatus());
        dto.setProfileCover(entity.getProfileCover());

        return dto;
    }

    private void mapToEntity(AccountDto dto, AccountEntity entity) {
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getPhoto() != null) entity.setPhoto(dto.getPhoto());
        if (dto.getAbout() != null) entity.setAbout(dto.getAbout());
        if (dto.getCity() != null) entity.setCity(dto.getCity());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getBirthDate() != null) entity.setBirthDate(dto.getBirthDate());
        if (dto.getLastOnlineTime() != null) entity.setLastOnlineTime(dto.getLastOnlineTime());
        if (dto.getIsBlocked() != null) entity.setIsBlocked(dto.getIsBlocked());
        if (dto.getIsDeleted() != null) entity.setIsDeleted(dto.getIsDeleted());
        if (dto.getPhotoName() != null) entity.setPhotoName(dto.getPhotoName());
        if (dto.getEmojiStatus() != null) entity.setEmojiStatus(dto.getEmojiStatus());
        if (dto.getProfileCover() != null) entity.setProfileCover(dto.getProfileCover());

        entity.setUpdatedOn(LocalDateTime.now());
    }
}
