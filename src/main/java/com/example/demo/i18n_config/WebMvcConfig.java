package com.example.demo.i18n_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LocaleResolverUtil customLocaleResolver;

    public WebMvcConfig(LocaleResolverUtil customLocaleResolver) {
        this.customLocaleResolver = customLocaleResolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return customLocaleResolver;
    }
}
