package com.soundlist.exception;

/**
 * Exceção lançada pelo Service quando um recurso solicitado não é encontrado no banco.
 *
 * Estende RuntimeException (unchecked) para não forçar try/catch em toda cadeia de chamada.
 * O @RestControllerAdvice captura essa exceção e retorna HTTP 404 com mensagem descritiva.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
