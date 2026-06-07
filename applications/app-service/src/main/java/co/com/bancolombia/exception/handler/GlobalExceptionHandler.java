package co.com.bancolombia.exception.handler;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.exception.dto.ErrorResponse;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

/**
 * Global exception handler for the entire application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles franchise not found exceptions
     */
    @ExceptionHandler(FranchiseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFranchiseNotFoundException(
            FranchiseNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Franchise Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Franchise not found")
                .errorCode("FRANCHISE_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles branch not found exceptions
     */
    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBranchNotFoundException(
            BranchNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Branch Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Branch not found")
                .errorCode("BRANCH_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles product not found exceptions
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Product Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Product not found")
                .errorCode("PRODUCT_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles database exceptions
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex,
            ServerWebExchange exchange) {

        log.error("Database Error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Service Unavailable")
                .message("Error conectado a base de datos")
                .errorCode(ex.getErrorCode())
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    /**
     * Handles generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected Error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado procesando la solicitud")
                .errorCode("INTERNAL_ERROR")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
