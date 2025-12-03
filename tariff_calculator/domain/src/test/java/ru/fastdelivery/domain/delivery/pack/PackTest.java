package ru.fastdelivery.domain.delivery.pack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.dimension.OuterDimensions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackTest {
    @Test
    @DisplayName("Если вес в пределах нормы -> создается объект")
    void ifWeightIsValid_thenCreateObject() {
        Weight weight = new Weight(BigDecimal.valueOf(99_999).toBigInteger());
        OuterDimensions dimensions = new OuterDimensions(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100)
        );
        Pack pack = new Pack(weight, dimensions);
        assertEquals(weight, pack.weight());
        assertEquals(dimensions, pack.outerDimensions());
    }
}
