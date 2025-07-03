package com.example.demo.i18n_config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    private final LocaleResolverUtil localeResolver;
    private final ObjectMapper objectMapper;
    private final Map<String, Map<String, String>> translationsCache = new ConcurrentHashMap<>();

    private static final String TRANSLATION_FILE_PATH_PREFIX = "classpath:i18n/";
    private static final String TRANSLATION_FILE_SUFFIX = ".json";

    @Autowired
    public TranslationService(@Qualifier("customLocaleResolver") LocaleResolverUtil localeResolver, ObjectMapper objectMapper) {
        this.localeResolver = localeResolver;
        this.objectMapper = objectMapper;
        preloadTranslations();
    }

    private void preloadTranslations() {
        logger.info("Preloading translations...");
        localeResolver.getSupportedLocales().forEach(locale -> {
            String lang = localeResolver.getLanguageFromLocale(locale);
            loadTranslations(lang);
            logger.info("Loaded translations for language: {}", lang);
        });
        // Ensure default is also loaded if not explicitly supported in list (e.g. "en" if default is Locale.ENGLISH)
        String defaultLang = localeResolver.getLanguageFromLocale(localeResolver.getPublicDefaultLocale());
        if (!translationsCache.containsKey(defaultLang)) {
             loadTranslations(defaultLang);
             logger.info("Loaded translations for default language: {}", defaultLang);
        }
    }

    private Map<String, String> loadTranslations(String lang) {
        return translationsCache.computeIfAbsent(lang, l -> {
            String filePath = TRANSLATION_FILE_PATH_PREFIX + l + TRANSLATION_FILE_SUFFIX;
            try {
                logger.info("Attempting to load translation file from: {}", filePath);
                InputStream inputStream = getClass().getResourceAsStream("/i18n/" + l + ".json");
                if (inputStream == null) {
                    logger.error("Translation file not found: {}", filePath);
                    // Fallback to English if specific language file not found
                    if (!"en".equals(l)) {
                        logger.warn("Falling back to English for language: {}", l);
                        return loadTranslations("en");
                    }
                    return Collections.emptyMap();
                }
                Map<String, String> messages = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});
                logger.info("Successfully loaded translations for language: {} from {}", l, filePath);
                return messages;
            } catch (IOException e) {
                logger.error("Failed to load or parse translation file: " + filePath, e);
                // Fallback to English on error
                if (!"en".equals(l)) {
                    logger.warn("Falling back to English for language due to error: {}", l);
                    return loadTranslations("en");
                }
                return Collections.emptyMap();
            }
        });
    }

    public String getTranslatedMessage(String messageKey, HttpServletRequest request) {
        Locale userLocale = localeResolver.resolveLocale(request);
        String lang = localeResolver.getLanguageFromLocale(userLocale);

        Map<String, String> messages = translationsCache.get(lang);
        if (messages == null) {
            logger.warn("No translations loaded for language: {}. Attempting to load now.", lang);
            messages = loadTranslations(lang); // Attempt to load if not preloaded for some reason
        }

        String message = messages.get(messageKey);
        if (message == null) {
            logger.warn("Message key '{}' not found for language '{}'. Trying default language.", messageKey, lang);
            // Try fallback to default language (English)
            String defaultLang = localeResolver.getLanguageFromLocale(localeResolver.getPublicDefaultLocale());
            messages = translationsCache.get(defaultLang);
            if (messages != null) {
                message = messages.get(messageKey);
            }
        }

        return message != null ? message : "Missing translation for: " + messageKey; // Fallback message
    }

    // Overloaded method for when HttpServletRequest is not available or needed
    public String getTranslatedMessage(String messageKey, Locale locale) {
        String lang = localeResolver.getLanguageFromLocale(locale);
        Map<String, String> messages = translationsCache.get(lang);
         if (messages == null) {
            logger.warn("No translations loaded for language: {}. Attempting to load now.", lang);
            messages = loadTranslations(lang);
        }

        String message = messages.get(messageKey);
         if (message == null) {
            logger.warn("Message key '{}' not found for language '{}'. Trying default language.", messageKey, lang);
            String defaultLang = localeResolver.getLanguageFromLocale(localeResolver.getPublicDefaultLocale());
            messages = translationsCache.get(defaultLang);
            if (messages != null) {
                message = messages.get(messageKey);
            }
        }
        return message != null ? message : "Missing translation for: " + messageKey;
    }
}
