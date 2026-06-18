package com.soundlist.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Record padronizado para respostas de erro da API.
 *
 * @JsonInclude(NON_NULL): campos nulos (como 'fieldErrors') não aparecem no JSON,
 * mantendo o corpo limpo para erros 404 e 500 que não têm erros de campo.
 *
 * Para erros 400, o campo 'fieldErrors' é populado com a lista de campos inválidos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {

    /**
     * Record interno para erros de validação por campo (usado em respostas 400).
     */
    public record FieldError(
            String field,
            String message
    ) {}
}
