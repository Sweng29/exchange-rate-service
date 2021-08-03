package com.scalable.exchange.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_MESSAGE = "Server error. Something went wrong.";
    private static final String TIMESTAMP = "timestamp";
    private static final String MESSAGE = "message";

    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> badRequestException(BadRequestException e) {

        String message =
                e.getMessage() != null
                        ? e.getMessage()
                        : DEFAULT_MESSAGE;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<Object> bindErrorHandler(BindException e) {

        StringBuffer message = new StringBuffer();

        if (!ObjectUtils.isEmpty(e.getAllErrors())) {
            message.append(e.getAllErrors().get(0).getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, message);

        return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
    }

    @ResponseBody
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<Object> exchangeRateServiceExceptionHandler(Exception e) {

        String message =
                e.getMessage() != null
                        ? e.getMessage()
                        : DEFAULT_MESSAGE;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, message);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(NoDataFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> noDataFoundExceptionHandler(NoDataFoundException e) {
        String message =
                e.getMessage() != null
                        ? e.getMessage()
                        : DEFAULT_MESSAGE;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, message);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> globalExceptionHandler(Exception e) {

        String message =
                e.getMessage() != null
                        ? e.getMessage()
                        : DEFAULT_MESSAGE;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, message);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
