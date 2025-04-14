package dev.noman.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // Injecting the value of app.url from application.properties or application.yml
    @Value("${app.url}")
    private String appUrl;

    public String getAppUrl() {
        return appUrl;
    }
}
