package ru.skillbox.socialnetwork.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.account.dto.*;
import ru.skillbox.socialnetwork.account.dto.AccountStatus;
import ru.skillbox.socialnetwork.account.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "ACCOUNT_SERVICE", description = "Операции с аккаунтами пользователей")
public class AccountController {

    private final AccountService accountService;

    // GET /api/v1/account/me → получить свой профиль
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить текущий аккаунт", security = @SecurityRequirement(name = "bearerAuth"))
    public AccountDto getCurrentAccount() {
        log.info("🔍 [CONTROLLER] GET /me - Получение текущего аккаунта");
        UUID userId = getCurrentUserIdFromSecurityContext();
        AccountDto result = accountService.getAccountById(userId);
        log.info("✅ [CONTROLLER] Текущий аккаунт получен: {}", result.getEmail());
        return result;
    }

    // PUT /api/v1/account/me → обновить свой профиль
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить свой профиль", security = @SecurityRequirement(name = "bearerAuth"))
    public AccountDto updateCurrentAccount(@RequestBody AccountDto dto) {
        log.info("✏️ [CONTROLLER] PUT /me - Обновление текущего аккаунта: {}", dto.getEmail());
        UUID userId = getCurrentUserIdFromSecurityContext();
        AccountDto result = accountService.updateCurrentAccount(userId, dto);
        log.info("✅ [CONTROLLER] Аккаунт обновлен: {}", result.getEmail());
        return result;
    }

    // DELETE /api/v1/account/me → удалить свой аккаунт
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить свой аккаунт", security = @SecurityRequirement(name = "bearerAuth"))
    public String deleteCurrentAccount() {
        log.info("🗑️ [CONTROLLER] DELETE /me - Удаление текущего аккаунта");
        UUID userId = getCurrentUserIdFromSecurityContext();
        accountService.deleteAccount(userId);
        log.info("✅ [CONTROLLER] Аккаунт удален: {}", userId);
        return "Account deleted successfully";
    }

    // GET /api/v1/account/{id} → получить аккаунт по ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить аккаунт по ID")
    public AccountDto getAccountById(@PathVariable UUID id) {
        log.info("🔍 [CONTROLLER] GET /{} - Получение аккаунта по ID", id);
        AccountDto result = accountService.getAccountById(id);
        log.info("✅ [CONTROLLER] Аккаунт найден: {} {}", result.getFirstName(), result.getLastName());
        return result;
    }

    // GET /api/v1/account → получить всех пользователей
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить всех пользователей")
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        log.info("📋 [CONTROLLER] GET / - Получение всех пользователей: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AccountDto> result = accountService.getAll(pageable);
        log.info("✅ [CONTROLLER] Найдено {} пользователей", result.getTotalElements());
        return result;
    }

    // POST /api/v1/account → создать аккаунт
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Создать новый аккаунт")
    public AccountDto createAccount(@RequestBody AccountDto dto) {
        log.info("🆕 [CONTROLLER] POST / - Создание нового аккаунта: {}", dto.getEmail());
        AccountDto result = accountService.createAccount(dto);
        log.info("✅ [CONTROLLER] Аккаунт создан: id={}", result.getId());
        return result;
    }

    // PUT /api/v1/account/block/{id} → заблокировать аккаунт
    @PutMapping("/block/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Блокировать аккаунт")
    public String blockAccount(@PathVariable UUID id) {
        log.info("🚫 [CONTROLLER] PUT /block/{} - Блокировка аккаунта", id);
        accountService.blockAccount(id);
        log.info("✅ [CONTROLLER] Аккаунт заблокирован: {}", id);
        return "Account blocked successfully";
    }

    // DELETE /api/v1/account/block/{id} → разблокировать аккаунт
    @DeleteMapping("/block/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Разблокировать аккаунт")
    public String unblockAccount(@PathVariable UUID id) {
        log.info("✅ [CONTROLLER] DELETE /block/{} - Разблокировка аккаунта", id);
        accountService.unblockAccount(id);
        log.info("✅ [CONTROLLER] Аккаунт разблокирован: {}", id);
        return "Account unblocked successfully";
    }

    // POST /api/v1/account/searchByFilter → поиск по фильтру
    @PostMapping("/searchByFilter")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск аккаунтов по фильтрам")
    public Page<AccountDto> searchAccountsByFilter(@RequestBody AccountByFilterDto filterDto) {
        log.info("🔍 [CONTROLLER] POST /searchByFilter - Поиск по фильтру");

        // ✅ Вызываем сервис (он возвращает Page<AccountDto>)
        Page<AccountDto> resultPage = accountService.searchByFilter(filterDto);

        log.info("✅ [CONTROLLER] Найдено {} аккаунтов по фильтру", resultPage.getTotalElements());
        return resultPage;
    }

    //
    @PostMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Найти аккаунты по списку ID")
    public List<AccountDto> findAccountsByIds(@RequestBody List<UUID> ids) {
        log.info("🔍 [CONTROLLER] POST /find - Поиск по {} ID", ids.size());
        List<AccountDto> result = accountService.find(ids);
        log.info("✅ [CONTROLLER] Найдено {} аккаунтов", result.size());
        return result;
    }

    // POST /api/v1/account/accountIds → найти по списку ID
    @GetMapping("/accountIds")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить аккаунты по списку ID с пагинацией")
    public Page<AccountDto> getAccountsByIds(@RequestParam("ids") List<UUID> ids, Pageable pageable) {
        log.info("📋 GET /accountIds - Поиск по {} ID, page={}, size={}", ids.size(), pageable.getPageNumber(), pageable.getPageSize());
        Page<AccountDto> result = accountService.getAccountsByIds(ids, pageable);
        log.info("✅ Найдено {} аккаунтов", result.getContent().size());
        return result;
    }

    private Sort buildSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            String property;
            Sort.Direction direction = Sort.Direction.ASC;

            if (param.contains(",")) {
                String[] parts = param.split(",", 2);
                property = parts[0].trim();
                try {
                    direction = Sort.Direction.fromString(parts[1].trim());
                } catch (IllegalArgumentException e) {
                    direction = Sort.Direction.ASC;
                }
            } else {
                property = param.trim();
            }

            orders.add(Sort.Order.by(property).with(direction));
        }
        return Sort.by(orders);
    }

    // GET /api/v1/account/search → поиск по строке

    @GetMapping("/search")
    public PageAccountDto searchAccounts(
            @RequestParam Map<String, String> queryParams
    ) {
        log.info("📬 Получены параметры: {}", queryParams);

        // Если есть "0=size=3", парсим её
        String rawParam = queryParams.get("0");
        if (rawParam != null && rawParam.contains("=")) {
            for (String pair : rawParam.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    queryParams.put(parts[0], parts[1]); // size=3 → key="size", value="3"
                }
            }
        }

        // Теперь извлекаем нормальные значения
        List<UUID> ids = null;
        String author = queryParams.get("author");
        String firstName = queryParams.get("firstName");
        String lastName = queryParams.get("lastName");
        String city = queryParams.get("city");
        String country = queryParams.get("country");
        Boolean isBlocked = getBoolean(queryParams.get("isBlocked"));
        Boolean isDeleted = getBoolean(queryParams.get("isDeleted"));
        Integer ageTo = getInteger(queryParams.get("ageTo"));
        Integer ageFrom = getInteger(queryParams.get("ageFrom"));
        Integer page = getInteger(queryParams.get("page")) != null ? getInteger(queryParams.get("page")) : 0;
        Integer size = getInteger(queryParams.get("size")) != null ? getInteger(queryParams.get("size")) : 10;

        log.info("📋 Распаршенные параметры: author={}, firstName={}, lastName={}, city={}, country={}, " +
                        "isBlocked={}, isDeleted={}, ageTo={}, ageFrom={}, page={}, size={}",
                author, firstName, lastName, city, country, isBlocked, isDeleted, ageTo, ageFrom, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AccountDto> result = accountService.searchWithPagination(
                ids, author, firstName, lastName, city, country,
                isBlocked, isDeleted, ageTo, ageFrom, pageable
        );

        return convertToPageAccountDto(result);
    }

    private Boolean getBoolean(String value) {
        return value != null && !value.isEmpty() ? Boolean.parseBoolean(value) : null;
    }

    private Integer getInteger(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }



    // GET /api/v1/account/status/{id} → получить статус аккаунта по ID

    @GetMapping("/status/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить статус аккаунта по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public AccountStatus getAccountStatus(@PathVariable UUID id) {
        log.info("📊 [CONTROLLER] GET /status/{} - Получение статуса аккаунта", id);
        AccountStatus status = accountService.getAccountStatus(id);
        log.info("✅ [CONTROLLER] Статус аккаунта для ID {}: {}", id, status);
        return status;
    }

    // === Приватные вспомогательные методы ===
    /**
     * Получает текущий userId из SecurityContext
     */
    private UUID getCurrentUserIdFromSecurityContext() {
        // Получаем аутентификацию из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем, что пользователь аутентифицирован
        if (authentication == null) {
            log.error("❌ [CONTROLLER] SecurityContext не содержит аутентификации");
            throw new AccessDeniedException("User not authenticated - no authentication found");
        }

        if (!authentication.isAuthenticated()) {
            log.error("❌ [CONTROLLER] Пользователь не аутентифицирован");
            throw new AccessDeniedException("User not authenticated");
        }

        // Логируем информацию для отладки
        log.info("🔐 [CONTROLLER] Principal type: {}", authentication.getPrincipal().getClass().getSimpleName());
        log.info("🔐 [CONTROLLER] Authentication name: {}", authentication.getName());
        log.info("🔐 [CONTROLLER] Authorities: {}", authentication.getAuthorities());

        try {
            // Извлекаем userId из authentication
            String userIdStr = authentication.getName();

            // Проверяем, что userId не пустой
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                log.error("❌ [CONTROLLER] UserId is null or empty in authentication");
                throw new AccessDeniedException("User ID not found in security context");
            }

            // Конвертируем в UUID
            UUID userId = UUID.fromString(userIdStr);
            log.info("✅ [CONTROLLER] Успешно извлечен UserId: {}", userId);

            return userId;

        } catch (IllegalArgumentException e) {
            log.error("❌ [CONTROLLER] Неверный формат UserId в SecurityContext: {}", e.getMessage());
            throw new AccessDeniedException("Invalid user ID format in security context");
        } catch (Exception e) {
            log.error("❌ [CONTROLLER] Неожиданная ошибка при извлечении UserId: {}", e.getMessage());
            throw new AccessDeniedException("Cannot extract user ID from security context: " + e.getMessage());
        }
    }

    /**
     * Конвертация Page<AccountDto> → PageAccountDto
     */
//    private PageAccountDto convertToPageAccountDto(Page<AccountDto> page) {
//        PageAccountDto dto = new PageAccountDto();
//        dto.setContent(page.getContent());
//        dto.setTotalElements(page.getTotalElements());
//        dto.setTotalPages(page.getTotalPages());
//        dto.setSize(page.getSize());
//        dto.setNumber(page.getNumber());
//        dto.setFirst(page.isFirst());
//        dto.setLast(page.isLast());
//        dto.setNumberOfElements(page.getNumberOfElements());
//        dto.setEmpty(page.isEmpty());
//
//        // TODO: Добавить реализацию для sort и pageable объектов
//        dto.setSort(new SortObject());
//        dto.setPageable(new PageableObject());
//
//        return dto;
//    }
    private PageAccountDto convertToPageAccountDto(Page<AccountDto> page) {
        return new PageAccountDto(
                (int) page.getTotalElements(),  // Integer totalElements
                page.getTotalPages(),           // Integer totalPages
                page.getSize(),                 // Integer size
                page.getContent(),              // List<AccountDto> content
                page.getNumber(),               // Integer number
                new SortObject(),               // SortObject sort (заглушка)
                page.isFirst(),                 // Boolean first
                page.isLast(),                  // Boolean last
                page.getNumberOfElements(),     // Integer numberOfElements
                new PageableObject(),           // PageableObject pageable (заглушка)
                page.isEmpty()                  // Boolean empty
        );
    }
}