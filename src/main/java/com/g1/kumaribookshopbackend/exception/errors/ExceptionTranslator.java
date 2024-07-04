package com.g1.kumaribookshopbackend.exception.errors;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.g1.kumaribookshopbackend.exception.AlreadyExistException;
import com.g1.kumaribookshopbackend.exception.JdbcException;
import com.g1.kumaribookshopbackend.exception.NotFoundException;
import com.g1.kumaribookshopbackend.exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException notFoundException){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),notFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationException(ValidateException validateException){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),validateException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> jdbcException(JdbcException jdbcException){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),jdbcException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),exception.getMessage());
        log.error("Internal Server Error : " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAlreadyExistException(AlreadyExistException alreadyExistException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), alreadyExistException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException tokenExpiredException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), tokenExpiredException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException badRequestException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), badRequestException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
