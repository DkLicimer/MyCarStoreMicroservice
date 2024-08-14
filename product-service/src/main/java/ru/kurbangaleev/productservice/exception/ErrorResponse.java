package ru.kurbangaleev.productservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private List<ValidationError> errors;

    public ErrorResponse(String field, String message) {
    }

    public void addValidationError(String field, String message) {
        if(errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ValidationError {
        private String field;
        private String message;
    }
}
