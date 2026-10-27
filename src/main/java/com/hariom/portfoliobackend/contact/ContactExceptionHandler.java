package com.hariom.portfoliobackend.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GLOBAL ERROR HANDLER for @RestController classes.
 *
 * Without it, a failed @Valid check becomes a giant default HTML error
 * page. With it, a bad request returns a small predictable JSON like:
 *   { "status": 400, "error": "Validation failed", "fields": { "email": "Enter a valid email address" } }
 *
 * The frontend reads `fields` to show each message under the right input.
 */
@RestControllerAdvice
public class ContactExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),                    // "name" / "email" / "message"
                fe -> fe.getDefaultMessage(),           // our message from the annotation
                (first, second) -> first                // keep the first on duplicates
            ));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Validation failed");
        body.put("fields", fields);
        body.put("timestamp", Instant.now().toString());
        return body;
    }

    /** Unknown path (e.g. GET / on this pure API) -> 404 JSON, not a 500. */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NoResourceFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("error", "No such endpoint. The API lives under /api — try /api/contact/health");
        body.put("timestamp", Instant.now().toString());
        return body;
    }

    /** Anything unexpected -> 500 with a clean JSON body (details stay in the console log). */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUnexpected(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 500);
        body.put("error", "Something went wrong. Please try again.");
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}
