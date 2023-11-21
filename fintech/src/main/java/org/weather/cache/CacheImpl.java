package org.weather.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

@Component
public class CacheImpl implements Cache {
    private final Map<Object, Node> cacheMap;
    private final LinkedList<Node> cacheList;
    private final CacheProperties cacheProperties;
    private final Object lock;

    public CacheImpl(CacheProperties cacheProperties) {
        this.cacheMap = new HashMap<>();
        this.cacheList = new LinkedList<>();
        this.cacheProperties = cacheProperties;
        this.lock = new Object();
    }

    @Override
    public <T> Optional<T> get(Object key, Class<T> valueType) {
        synchronized (lock) {
            Node node = cacheMap.get(key);
            if (node == null || node.getData() == null || node.getData().get() == null || isExpired(node)) {
                return Optional.empty();
            }
            cacheList.remove(node);
            cacheList.addFirst(node);
            return Optional.ofNullable(valueType.cast(node.getData().get()));
        }
    }

    @Override
    public void put(Object key, Object value) {
        synchronized (lock) {
            Node node = cacheMap.get(key);
            if (node == null) {
                addNewValue(key, value);
            } else {
                node.getData().clear();
                node.setData(new SoftReference<>(value));
                node.setCreatedAt(LocalDateTime.now());
            }
            cleanUpCache();
        }
    }

    private void addNewValue(Object key, Object value) {
        synchronized (lock) {
            Node node = new Node<>(key, new SoftReference<>(value), LocalDateTime.now());
            cacheMap.put(node.key, node);
            cacheList.addFirst(node);
        }
    }

    private boolean isExpired(Node node) {
        LocalDateTime expirationTime = node.getCreatedAt().plus(cacheProperties.getTtl());
        return LocalDateTime.now().isAfter(expirationTime);
    }

    private void cleanUpCache() {
        synchronized (lock) {
            if (cacheList.size() > cacheProperties.getSize()) {
                Node nodeToRemove = cacheList.removeLast();
                cacheMap.remove(nodeToRemove.getKey());
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Node <K, V> {
        private final K key;
        private SoftReference<V> data;
        private LocalDateTime createdAt;
    }
}
