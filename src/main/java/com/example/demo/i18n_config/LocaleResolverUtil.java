package com.example.demo.i18n_config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component("customLocaleResolver")
public class LocaleResolverUtil extends AcceptHeaderLocaleResolver {

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            new Locale("en"),
            new Locale("uz"),
            new Locale("ru"),
            new Locale("uz", "CYRL") // Uzbek Cyrillic
    );

    public LocaleResolverUtil() {
        setSupportedLocales(SUPPORTED_LOCALES);
        setDefaultLocale(Locale.ENGLISH); // Default to English if no match
    }

    public Locale resolveLocale(HttpServletRequest request) {
        String acceptLanguageHeader = request.getHeader("Accept-Language");
        if (acceptLanguageHeader == null || acceptLanguageHeader.isEmpty()) {
            return getDefaultLocale();
        }

        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguageHeader);
        Locale locale = Locale.lookup(list, SUPPORTED_LOCALES);

        if (locale == null) {
            // Handle specific case for uz-Cyrl that Locale.lookup might miss
            if (acceptLanguageHeader.toLowerCase().contains("uz-cyrl")) {
                return new Locale("uz", "CYRL");
            }
            return getDefaultLocale();
        }
        return locale;
    }

    public String getLanguageFromLocale(Locale locale) {
        if (locale.getLanguage().equals("uz") && "CYRL".equalsIgnoreCase(locale.getCountry())) {
            return "uz_cyrl";
        }
        return locale.getLanguage();
    }

    public Locale getPublicDefaultLocale() {
        Locale defaultLocale = getDefaultLocale(); // This is fine as it's within the class itself
        return defaultLocale != null ? defaultLocale : Locale.ENGLISH; // Ensure null safety, though super sets it
    }
}
