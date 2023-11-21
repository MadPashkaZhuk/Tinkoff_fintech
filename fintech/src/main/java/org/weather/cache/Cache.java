package org.weather.cache;

import java.util.Optional;

public interface Cache {
    <T> Optional<T> get(Object key, Class<T> valueType);

    void put(Object key, Object value);
}
