package ru.skillbox.socialnetwork.friend.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AffinityServiceImpl implements AffinityService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String AFFINITY_KEY_PREFIX = "affinity:"; // prefix для ключа в Redis
    private static final int MAX_RECOMMENDATIONS = 1000; // ограничение на размер

    @Override
    public void increaseAffinity(UUID userId, UUID targetId, Double weight) {
        String key = AFFINITY_KEY_PREFIX + userId;
        String targetIdStr = targetId.toString();

        // Атомарно инкрементируем и получаем флаг "был ли элемент новым"
        Double oldScore = redisTemplate.opsForZSet().score(key, targetIdStr);
        redisTemplate.opsForZSet().incrementScore(key, targetIdStr, weight);

        // Только для новых элементов проверяем размер
        if (oldScore == null) {
            trimToMaxSize(key);
        }
    }

    private void trimToMaxSize(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        if (size != null && size > MAX_RECOMMENDATIONS) {
            // Удаляем самые старые/непопулярные элементы
            long removeCount = size - MAX_RECOMMENDATIONS;
            redisTemplate.opsForZSet().removeRange(key, 0, removeCount - 1);
        }
    }
}
