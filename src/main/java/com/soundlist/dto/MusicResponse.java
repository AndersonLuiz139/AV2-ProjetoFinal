package com.soundlist.dto;

/**
 * DTO de SAÍDA para Music.
 *
 * Expõe playlistId em vez do objeto Playlist completo,
 * evitando referências circulares na serialização JSON
 * (Playlist → List<Music> → Playlist → ...).
 */
public record MusicResponse(
        Long id,
        String title,
        String artist,
        String genre,
        Integer duration,
        Long playlistId
) {}
