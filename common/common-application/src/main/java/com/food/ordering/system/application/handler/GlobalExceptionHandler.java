package com.food.ordering.system.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorDTO handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unexpected error.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    public ErrorDTO handleException(ValidationException validationException) {
        String exceptionMessage;

        if (validationException instanceof ConstraintViolationException constraintViolationException) {
            exceptionMessage = extractViolationsFromException(constraintViolationException);
            log.error(exceptionMessage, constraintViolationException);
        } else {
            exceptionMessage = validationException.getMessage();
            log.error(exceptionMessage, validationException);
        }

        return new ErrorDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(), exceptionMessage);
    }

    private String extractViolationsFromException(ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("--"));
    }

}
