package com.example.demo.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Languages {
    UZBEK,
    ENGLISH,
    RUSSIAN,
    TURKISH,
    SPANISH,
    FRENCH,
    HINDI,
    KOREAN,
    KIRGHIZ,
    ARABIC;


    public static List<String> getAllRoles() {
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
