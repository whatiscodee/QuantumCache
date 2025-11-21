package ru.whatiscode.quantumcache.optimization;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Singleton
public class CacheOptimizationEngine {

    private static final double LEARNING_RATE = 0.1;
    private static final double DECAY_FACTOR = 0.95;
    private static final int WARMUP_PERIOD = 1000;

    private final AtomicLong totalRequests;
    private final AtomicLong hitCount;

    private double optimalCacheSizeFactor;
    private double currentHitRate;


    public CacheOptimizationEngine() {
        this.totalRequests = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);

        this.optimalCacheSizeFactor = 0.7;
        this.currentHitRate = 0.0;
    }

    public void recordAccess(boolean isHit) {
        long total = totalRequests.incrementAndGet();

        if (isHit) {
            hitCount.incrementAndGet();
        }

        if (total % 100 == 0) {
            recalculateOptimalParameters();
        }
    }

    private void recalculateOptimalParameters() {
        long total = totalRequests.get();
        long hits = hitCount.get();

        if (total > WARMUP_PERIOD) {
            double newHitRatio = (double) hits / total;

            if (newHitRatio < currentHitRate) {
                optimalCacheSizeFactor = Math.min(0.95, optimalCacheSizeFactor + LEARNING_RATE);
            } else {
                optimalCacheSizeFactor = Math.max(0.3, optimalCacheSizeFactor * DECAY_FACTOR);
            }

            currentHitRate = newHitRatio;
            System.out.printf("Optimization update - Hit ratio: {:.3f}, Optimal factor: {:.3f}",
                    newHitRatio, optimalCacheSizeFactor);
        }
    }

    public long calculateOptimalSize(long availableMemory) {
        return availableMemory;
    }
}
