package cz.cvut.fel.pm2.FinanceMicroservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Configuration class for caching settings using Spring's ConcurrentMapCacheManager.
 */
@Configuration
public class CacheConfig {
    /**
     * Creates a cache manager for the 'goals' cache.
     *
     * @return ConcurrentMapCacheManager configured with a single cache named 'goals'
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("goals");
    }
}

