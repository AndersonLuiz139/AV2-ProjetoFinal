package com.soundlist.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de ENTRADA para criação e atualização de Playlist.
 *
 * Records são imutáveis por natureza e ideais para DTOs:
 * geram automaticamente construtor, getters, equals, hashCode e toString.
 *
 * @NotBlank: garante que name não seja nulo, vazio nem só espaços.
 * A validação é acionada pelo @Valid no Controller.
 */
public record PlaylistRequest(

        @NotBlank(message = "O nome da playlist é obrigatório")
        String name,

        // description é opcional — sem anotação de validação
        String description
) {}
