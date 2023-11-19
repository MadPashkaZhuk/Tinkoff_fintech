package org.weather.cache;

import org.springframework.stereotype.Component;
import org.weather.dto.WeatherDTO;

import java.lang.ref.SoftReference;
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
            if (node == null || node.getData() == null || node.getData().get() == null) {
                return Optional.empty();
            }
            cacheList.remove(node);
            cacheList.addFirst(node);
            return Optional.ofNullable(node.getData().get());
        }
    }

    public void addWeather(WeatherDTO weatherDTO) {
        synchronized (lock) {
            Node node = new Node(weatherDTO.getCity().getName(), new SoftReference<>(weatherDTO));
            cacheMap.put(node.location, node);
            cacheList.addFirst(node);

            if (cacheList.size() > cacheProperties.getSize()) {
                Node nodeToRemove = cacheList.removeLast();
                cacheMap.remove(nodeToRemove.getLocation());
            }
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
            }
        }
    }

    private static class Node {
        private final String location;
        private SoftReference<WeatherDTO> data;

        public Node(String location, SoftReference<WeatherDTO> data) {
            this.location = location;
            this.data = data;
        }

        public String getLocation() {
            return location;
        }

        public SoftReference<WeatherDTO> getData() {
            return data;
        }

        public void setData(SoftReference<WeatherDTO> data) {
            this.data = data;
        }
    }
}
