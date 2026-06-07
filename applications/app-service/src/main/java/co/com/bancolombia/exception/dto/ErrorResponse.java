package co.com.bancolombia.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO for generic error responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private String requestId;

    /**
     * Simplified builder with automatic timestamp
     */
    public static ErrorResponse of(int status, String message, String errorCode) {
        return ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    /**
     * Complete Builder
     */
    public static ErrorResponse of(int status, String error, String message, String errorCode, String path, String requestId) {
        return ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .status(status)
                .error(error)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .requestId(requestId)
                .build();
    }
}

