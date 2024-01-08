package ntou.auction.spring.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ntou.auction.spring.chat.exception.MessageNotFound;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        String errorMessage = constraintViolation.getMessage();
        Map<String, String> response = Collections.singletonMap("message", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MessageNotFound.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFoundException(MessageNotFound ex) {
        String errorMessage = ex.getMessage();
        Map<String, String> response = Collections.singletonMap("message", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getDefaultMessage());
        }
        Map<String, String> response = Collections.singletonMap("message", StringUtils.join(errors,'\n'));
        return handleExceptionInternal(
                ex, response, headers, status, request);
    }
}