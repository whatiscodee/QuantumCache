package ru.whatiscode.quantumcache.core;

import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Map;

@Slf4j
@Singleton
public class EnhancedCacheManager<K, V> {

    private final Map<String, LoadingCache<K, V>> cacheRegistry;
    private final CacheConfiguration configuration;
    private final CacheMetricsCollector metricsCollector;
    private final HashOptimizer hashOptimizer;


}
