package com.staff.kasir.response;



import com.staff.kasir.entity.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private Result result;
//javax.validation.ConstraintViolationException.class
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result> inputValidationException(ConstraintViolationException ex, WebRequest request) {

        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

        result = new Result();
        result.setCode(400);
        result.setMessage("");
        result.setSuccess(false);
        result.setData(errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        result = new Result();
        result.setCode(400);
        result.setMessage(ex.getMessage());
        result.setSuccess(false);
        result.setData(errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", status.value());

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        responseBody.put("errors", errors);

        result = new Result();
        result.setCode(status.value());
        result.setMessage("");
        result.setSuccess(false);
        result.setData(errors);

        return new ResponseEntity<>(result, headers, status);
    }

}
