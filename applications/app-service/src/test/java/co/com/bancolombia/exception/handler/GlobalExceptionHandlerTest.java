package co.com.bancolombia.exception.handler;

import co.com.bancolombia.exception.dto.ErrorResponse;
import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockServerHttpRequest req = MockServerHttpRequest
                .post("/api/v1/franchises/create")
                .build();
        exchange = MockServerWebExchange.from(req);
    }

    @Test
    void validationException_returnsBadRequestWithRealMessage() {
        ResponseEntity<ErrorResponse> resp = handler.handleValidationException(
                new ValidationException("Franchise name is required"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Franchise name is required");
        assertThat(body.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getPath()).isEqualTo("/api/v1/franchises/create");
        assertThat(body.getRequestId()).isNotNull();
    }

    @Test
    void illegalArgumentException_alsoMapsToBadRequest() {
        ResponseEntity<ErrorResponse> resp = handler.handleValidationException(
                new IllegalArgumentException("Product name is required"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Product name is required");
        assertThat(resp.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void franchiseNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handleFranchiseNotFoundException(
                new FranchiseNotFoundException("not found"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("FRANCHISE_NOT_FOUND");
    }

    @Test
    void branchNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handleBranchNotFoundException(
                new BranchNotFoundException("missing"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("BRANCH_NOT_FOUND");
    }

    @Test
    void productNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handleProductNotFoundException(
                new ProductNotFoundException("missing"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
    }

    @Test
    void databaseException_returns503() {
        ResponseEntity<ErrorResponse> resp = handler.handleDatabaseException(
                new DatabaseException("conn failed"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("DATABASE_ERROR");
    }

    @Test
    void genericException_returns500WithGenericMessage() {
        ResponseEntity<ErrorResponse> resp = handler.handleGlobalException(
                new RuntimeException("boom"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
        // The generic 500 hides the real exception message from the client
        assertThat(resp.getBody().getMessage()).contains("inesperado");
    }
}

