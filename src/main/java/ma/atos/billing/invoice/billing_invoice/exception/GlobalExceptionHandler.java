package ma.atos.billing.invoice.billing_invoice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ma.atos.billing.invoice.billing_invoice.dtos.InvoiceSearchCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        log.error("Resource not found : {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        log.error("Illegal argument exception : {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest()
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildValidationMessage)
                .toList();

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message("Paramètres invalides")
                .details(details)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest()
                .body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected error", ex);

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private String buildValidationMessage(FieldError error) {

        Object rejectedValue = error.getRejectedValue();

        String fieldName = error.getField();

        String defaultMessage = error.getDefaultMessage();

        if (defaultMessage != null &&
                defaultMessage.contains("Failed to convert")) {

            try {

                Class<?> fieldType = InvoiceSearchCriteria.class
                        .getDeclaredField(fieldName)
                        .getType();

                if (fieldType.isEnum()) {

                    Object[] enumConstants = fieldType.getEnumConstants();

                    String allowedValues = java.util.Arrays.toString(enumConstants);

                    return "La valeur '"
                            + rejectedValue
                            + "' est invalide pour le champ '"
                            + fieldName
                            + "'. Valeurs autorisées : "
                            + allowedValues;
                }

            } catch (NoSuchFieldException e) {

                return "Invalid value '"
                        + rejectedValue
                        + "' for field '"
                        + fieldName + "'";
            }
        }

        return defaultMessage;
    }

}