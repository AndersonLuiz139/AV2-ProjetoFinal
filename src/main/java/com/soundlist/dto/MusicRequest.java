package com.soundlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de ENTRADA para criação e atualização de Music.
 *
 * Validações Bean Validation aplicadas:
 *  - @NotBlank em title e artist: campos de texto obrigatórios
 *  - @NotNull em duration: não pode ser nulo
 *  - @Positive em duration: deve ser maior que zero (segundos positivos)
 *  - @NotNull em playlistId: a música deve pertencer a uma playlist
 *
 * Além das validações aqui, o MusicService valida se o playlistId
 * corresponde a uma Playlist existente no banco (regra de negócio → 404).
 */
public record MusicRequest(

        @NotBlank(message = "O título da música é obrigatório")
        String title,

        @NotBlank(message = "O nome do artista é obrigatório")
        String artist,

        // genre é opcional
        String genre,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser um valor positivo em segundos")
        Integer duration,

        @NotNull(message = "O ID da playlist é obrigatório")
        Long playlistId
) {}
