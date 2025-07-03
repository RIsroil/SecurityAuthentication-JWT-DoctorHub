package com.example.demo.i18n_config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // No need to inject LocaleResolverUtil here if we are configuring it directly in the bean method.
    // We can autowire it if other parts of WebMvcConfig need it, but for now, it's self-contained in the bean.

    @Bean
    public LocaleResolver localeResolver() {
        LocaleResolverUtil localeResolver = new LocaleResolverUtil();
        // Supported locales are already set in LocaleResolverUtil constructor
        localeResolver.setDefaultLocale(new Locale("uz")); // Setting default to Uzbek (Latin)
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages"); // base name for properties files
        messageSource.setDefaultEncoding("UTF-8");
        // By default, MessageSource uses the system locale if a message for the requested locale is not found.
        // To ensure Uzbek is the ultimate fallback if a key is missing even in English (after primary lookup failed),
        // this can be handled in how messages are retrieved or by ensuring `messages.properties` (Uzbek) is complete.
        // Spring's AcceptHeaderLocaleResolver (which LocaleResolverUtil extends) will handle falling back through
        // supported locales, and then its own default (which we set to 'uz').
        // MessageSource itself will use the locale provided by the LocaleResolver.
        // If a specific key is not found for a resolved locale, it can fallback to the default properties file (messages.properties).
        messageSource.setDefaultLocale(new Locale("uz")); // Reinforce Uzbek as the default for MessageSource itself.
        messageSource.setUseCodeAsDefaultMessage(true); // If true, returns the code itself if no message found. Set to false for production.
        return messageSource;
    }
}
