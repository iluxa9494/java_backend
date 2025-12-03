package ru.fastdelivery.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.delivery.AdditionalService;
import ru.fastdelivery.persistence.entity.AdditionalServiceEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdditionalServiceJpaAdapterTest {
    private AdditionalServiceJpaRepository repository;
    private AdditionalServiceJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(AdditionalServiceJpaRepository.class);
        adapter = new AdditionalServiceJpaAdapter(repository);
    }

    @Test
    @DisplayName("Преобразует сущности услуг в доменные объекты")
    void shouldMapEntitiesToDomainServices() {
        var entity = AdditionalServiceEntity.builder()
                .id(1)
                .name("Упаковка")
                .price(BigDecimal.valueOf(250))
                .priceType("fixed")
                .description("Премиум-упаковка")
                .build();
        when(repository.findAll()).thenReturn(List.of(entity));
        List<AdditionalService> result = adapter.getAll();
        assertEquals(1, result.size());
        AdditionalService service = result.get(0);
        assertEquals("Упаковка", service.getName());
        assertEquals(BigDecimal.valueOf(250), service.getPrice());
        assertEquals("fixed", service.getPriceType());
        assertEquals("Премиум-упаковка", service.getDescription());
        verify(repository, times(1)).findAll();
    }
}