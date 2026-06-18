package com.soundlist.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handler global de exceções para toda a API.
 *
 * @RestControllerAdvice: combina @ControllerAdvice + @ResponseBody.
 * Intercepta exceções lançadas em qualquer @RestController e retorna
 * respostas JSON padronizadas com o código HTTP correto.
 *
 * Hierarquia de tratamento:
 *  1. ResourceNotFoundException → 404 Not Found
 *  2. MethodArgumentNotValidException → 400 Bad Request (validação de campos)
 *  3. Exception → 500 Internal Server Error (fallback genérico)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de recurso não encontrado (404).
     *
     * Lançada pelo Service quando um id não existe no banco:
     *   throw new ResourceNotFoundException("Playlist com id 99 não encontrada")
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null  // sem fieldErrors para 404
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Trata erros de validação Bean Validation nos DTOs (400 Bad Request).
     *
     * Disparado quando o @Valid no Controller encontra campos inválidos.
     * Extrai cada FieldError do BindingResult e monta uma lista descritiva,
     * permitindo ao cliente saber exatamente quais campos falharam e por quê.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Extrai a lista de erros de campo do BindingResult da exceção
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de validação nos campos da requisição",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Fallback genérico para qualquer exceção não tratada acima (500).
     *
     * Garante que erros inesperados também retornem JSON ao cliente,
     * evitando respostas HTML padrão do Spring (Whitelabel Error Page).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro inesperado: " + ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
