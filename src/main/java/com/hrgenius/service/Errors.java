package com.hrgenius.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Errors {
    public static ResponseStatusException notFound(String what) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
    }
}
