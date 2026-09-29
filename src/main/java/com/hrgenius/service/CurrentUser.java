package com.hrgenius.service;

import com.hrgenius.model.AppUser;
import com.hrgenius.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Who is calling the API right now? */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final AppUserRepository users;

    public AppUser get() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown user"));
    }

    public Long employeeId() {
        Long id = get().getEmployeeId();
        if (id == null) throw new IllegalArgumentException("This login is not linked to an employee record");
        return id;
    }
}
