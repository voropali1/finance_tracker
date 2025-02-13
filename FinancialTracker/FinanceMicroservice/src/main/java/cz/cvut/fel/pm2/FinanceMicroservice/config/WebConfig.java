package cz.cvut.fel.pm2.FinanceMicroservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Configuration class for Web MVC settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configures CORS mappings for all endpoints.
     *
     * @param registry CorsRegistry instance to configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("authorization", "content-type", "x-auth-token")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
