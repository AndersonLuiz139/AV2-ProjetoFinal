package com.soundlist.dto;

import java.util.List;

/**
 * DTO de SAÍDA para Playlist.
 *
 * Inclui a lista de músicas da playlist como List<MusicResponse>,
 * permitindo que o cliente veja as músicas associadas em um único GET.
 *
 * Nenhuma entidade JPA é exposta diretamente na resposta — sempre via DTO.
 */
public record PlaylistResponse(
        Long id,
        String name,
        String description,
        List<MusicResponse> musics
) {}
