package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.models.entity.Person;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareUser implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.of("unknown");
        } else if (auth.getPrincipal() instanceof String) {
            return Optional.of("unknown");
        }
        Person auditor = (Person) auth.getPrincipal();
        return Optional.of(auditor.getUsername());
    }
}
