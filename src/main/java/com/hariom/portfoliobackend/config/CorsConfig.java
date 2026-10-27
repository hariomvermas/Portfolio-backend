package com.hariom.portfoliobackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS — the browser's "are other websites allowed to call me?" gate.
 *
 * When the React dev server on http://localhost:5173 calls
 * http://localhost:8080, the ports differ, so it counts as a
 * cross-origin request. Without the header below the browser blocks
 * the response (the API still ran — it is the REPLY that gets dropped).
 *
 * Allowed origins come from application.properties
 * (app.cors.allowed-origins) so dev and production can differ.
 *
 * Notes:
 * - allowedOriginPatterns lets one pattern cover localhost:5173 AND your
 *   Vercel URL without rebuilding for each.
 * - CRITICAL for production: when deployed behind a platform proxy the
 *   browser sends Origin as https://... — never http://, so keep the
 *   https entry in the properties file.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns(allowedOrigins)
            .allowedMethods("GET", "POST")
            .allowedHeaders("*")
            .maxAge(3600);
    }
}
