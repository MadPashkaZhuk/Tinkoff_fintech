package org.weather.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.weather.dto.WeatherDTO;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

@Component
public class WeatherCache {
    private final Map<String, Node> cacheMap;
    private final LinkedList<Node> cacheList;
    private final CacheProperties cacheProperties;
    private final Object lock;

    public WeatherCache(CacheProperties cacheProperties) {
        this.cacheMap = new HashMap<>();
        this.cacheList = new LinkedList<>();
        this.cacheProperties = cacheProperties;
        this.lock = new Object();
    }

    public Optional<WeatherDTO> getWeather(String location) {
        synchronized (lock) {
            Node node = cacheMap.get(location);
            if (node == null || node.getData() == null || node.getData().get() == null || isExpired(node)) {
                return Optional.empty();
            }
            cacheList.remove(node);
            cacheList.addFirst(node);
            return Optional.ofNullable(node.getData().get());
        }
    }

    public void addWeather(WeatherDTO weatherDTO) {
        synchronized (lock) {
            Node node = new Node(weatherDTO.getCity().getName(), new SoftReference<>(weatherDTO), LocalDateTime.now());
            cacheMap.put(node.location, node);
            cacheList.addFirst(node);
        }
    }

    public void updateWeather(WeatherDTO weatherDTO) {
        synchronized (lock) {
            Node node = cacheMap.get(weatherDTO.getCity().getName());
            if (node == null) {
                addWeather(weatherDTO);
            } else {
                node.getData().clear();
                node.setData(new SoftReference<>(weatherDTO));
                node.setCreated_at(LocalDateTime.now());
            }
            cleanUpCache();
        }
    }

    private boolean isExpired(Node node) {
        LocalDateTime expirationTime = node.getCreated_at().plusMinutes(cacheProperties.getTtl());
        return LocalDateTime.now().isAfter(expirationTime);
    }

    private void cleanUpCache() {
        synchronized (lock) {
            cacheList.removeIf(this::isExpired);
            cacheMap.entrySet().removeIf(entry -> isExpired(entry.getValue()));
            if (cacheList.size() > cacheProperties.getSize()) {
                Node nodeToRemove = cacheList.removeLast();
                cacheMap.remove(nodeToRemove.getLocation());
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Node {
        private final String location;
        private SoftReference<WeatherDTO> data;
        private LocalDateTime created_at;
    }
}
