package ru.fastdelivery.presentation.calc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.repository.UserRequestRepository;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.request.CoordinateDto;
import ru.fastdelivery.presentation.config.GeoProperties;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.util.List;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CalculateController.class)
@Import(CalculateController.class)
public class CalculateControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String URL_V1 = "/api/v1/calculate";
    private static final String URL_ALIAS = "/api/tariff-calculator";
    @MockBean
    TariffCalculateUseCase useCase;
    @MockBean
    CurrencyFactory currencyFactory;
    @MockBean
    GeoProperties geoProperties;
    @MockBean
    UserRequestRepository userRequestRepository;

    @Test
    @DisplayName("Валидный запрос — HTTP 200")
    void whenValidRequest_thenReturns200(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        var rub = new Currency("RUB", BigDecimal.ONE);
        var total = new Price(BigDecimal.valueOf(1000), rub);
        var minimal = new Price(BigDecimal.valueOf(500), rub);
        when(geoProperties.getMinLatitude()).thenReturn(BigDecimal.valueOf(45));
        when(geoProperties.getMaxLatitude()).thenReturn(BigDecimal.valueOf(65));
        when(geoProperties.getMinLongitude()).thenReturn(BigDecimal.valueOf(30));
        when(geoProperties.getMaxLongitude()).thenReturn(BigDecimal.valueOf(96));
        when(currencyFactory.create("RUB")).thenReturn(rub);
        when(useCase.calc(any())).thenReturn(total);
        when(useCase.minimalPrice()).thenReturn(minimal);
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(2000),
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );
        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("RUB"))
                .andExpect(jsonPath("$.totalPrice").value(1000))
                .andExpect(jsonPath("$.minimalPrice").value(500));
    }

    @Test
    @DisplayName("Alias endpoint /api/tariff-calculator — HTTP 200")
    void whenValidRequestToAlias_thenReturns200(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        var rub = new Currency("RUB", BigDecimal.ONE);
        var total = new Price(BigDecimal.valueOf(1000), rub);
        var minimal = new Price(BigDecimal.valueOf(500), rub);
        when(geoProperties.getMinLatitude()).thenReturn(BigDecimal.valueOf(45));
        when(geoProperties.getMaxLatitude()).thenReturn(BigDecimal.valueOf(65));
        when(geoProperties.getMinLongitude()).thenReturn(BigDecimal.valueOf(30));
        when(geoProperties.getMaxLongitude()).thenReturn(BigDecimal.valueOf(96));
        when(currencyFactory.create("RUB")).thenReturn(rub);
        when(useCase.calc(any())).thenReturn(total);
        when(useCase.minimalPrice()).thenReturn(minimal);
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(2000),
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );
        mockMvc.perform(post(URL_ALIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("RUB"))
                .andExpect(jsonPath("$.totalPrice").value(1000))
                .andExpect(jsonPath("$.minimalPrice").value(500));
    }

    @Test
    @DisplayName("Невалидный запрос — пустой список упаковок — HTTP 400")
    void whenInvalidRequest_thenReturns400(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        var request = new CalculatePackagesRequest(
                null,
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Вес больше 150000г — HTTP 400")
    void whenWeightTooLarge_thenReturns400(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        stubGeoBounds();
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(150001),
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Отрицательный вес — HTTP 400")
    void whenWeightNegative_thenReturns400(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        stubGeoBounds();
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(-1),
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Размер больше 1500мм — HTTP 400")
    void whenDimensionTooLarge_thenReturns400(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        stubGeoBounds();
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(2000),
                        BigDecimal.valueOf(1501),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Координаты вне диапазона — HTTP 400")
    void whenCoordinatesOutOfBounds_thenReturns400(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        stubGeoBounds();
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(2000),
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(400)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(10), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Округление габаритов до 50мм")
    void whenNonRoundedDimensions_thenNormalizedToNearestFifty(@org.springframework.beans.factory.annotation.Autowired MockMvc mockMvc) throws Exception {
        stubGeoBounds();
        var rub = new Currency("RUB", BigDecimal.ONE);
        var total = new Price(BigDecimal.valueOf(1000), rub);
        var minimal = new Price(BigDecimal.valueOf(500), rub);
        when(currencyFactory.create("RUB")).thenReturn(rub);
        when(useCase.calc(any())).thenReturn(total);
        when(useCase.minimalPrice()).thenReturn(minimal);

        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigDecimal.valueOf(2000),
                        BigDecimal.valueOf(345),
                        BigDecimal.valueOf(589),
                        BigDecimal.valueOf(234)
                )),
                "RUB",
                new CoordinateDto(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176)),
                new CoordinateDto(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351))
        );

        mockMvc.perform(post(URL_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<ru.fastdelivery.domain.delivery.shipment.Shipment> captor =
                ArgumentCaptor.forClass(ru.fastdelivery.domain.delivery.shipment.Shipment.class);
        verify(useCase, times(1)).calc(captor.capture());
        var pack = captor.getValue().packages().get(0);
        assertThat(pack.outerDimensions().length()).isEqualByComparingTo(BigDecimal.valueOf(350));
        assertThat(pack.outerDimensions().width()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(pack.outerDimensions().height()).isEqualByComparingTo(BigDecimal.valueOf(250));
    }

    private void stubGeoBounds() {
        when(geoProperties.getMinLatitude()).thenReturn(BigDecimal.valueOf(45));
        when(geoProperties.getMaxLatitude()).thenReturn(BigDecimal.valueOf(65));
        when(geoProperties.getMinLongitude()).thenReturn(BigDecimal.valueOf(30));
        when(geoProperties.getMaxLongitude()).thenReturn(BigDecimal.valueOf(96));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
