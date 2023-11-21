package org.weather.cache;

import java.util.Optional;
import java.util.function.Supplier;

public class CacheHelper {
    public static <T> T getFromCache(Cache cache, Object key, Supplier<? extends T> supplier) {
        Optional<T> cachedValue = cache.get(key, (Class<T>) supplier.get().getClass());
        if (cachedValue.isPresent()) {
            return cachedValue.get();
        }
        T newValue = supplier.get();
        cache.put(key, newValue);
        return newValue;
    }
}
