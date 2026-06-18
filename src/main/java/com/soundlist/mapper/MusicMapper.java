package com.soundlist.mapper;

import com.soundlist.dto.MusicRequest;
import com.soundlist.dto.MusicResponse;
import com.soundlist.model.Music;
import com.soundlist.model.Playlist;
import org.mapstruct.*;

/**
 * Mapper MapStruct para conversão entre Music (entidade) e seus DTOs.
 *
 * O ponto mais importante aqui é o mapeamento de playlistId:
 *
 * - MusicRequest.playlistId (Long) → Music.playlist (Playlist):
 *   o MapStruct não sabe converter Long → Playlist automaticamente,
 *   então usamos um método auxiliar (@Named) para isso.
 *
 * - Music.playlist.id (Long aninhado) → MusicResponse.playlistId (Long):
 *   aqui usamos source = "playlist.id" para navegar no objeto aninhado.
 */
@Mapper(componentModel = "spring")
public interface MusicMapper {

    /**
     * Converte MusicRequest → Music.
     *
     * O campo 'playlist' na entidade é um objeto Playlist, mas o request
     * só traz o 'playlistId' (Long). Usamos @Mapping com qualifiedByName
     * para chamar o método auxiliar que constrói um objeto Playlist
     * somente com o id preenchido.
     *
     * Nota: o Service já validou que esse playlistId existe no banco antes
     * de chamar o mapper, então aqui apenas montamos a referência.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlist", source = "playlistId", qualifiedByName = "playlistIdToPlaylist")
    Music toEntity(MusicRequest request);

    /**
     * Converte Music → MusicResponse.
     *
     * "playlist.id" navega pelo objeto aninhado Music.playlist para extrair
     * o id e mapeá-lo em MusicResponse.playlistId.
     */
    @Mapping(target = "playlistId", source = "playlist.id")
    MusicResponse toResponse(Music music);

    /**
     * Atualiza uma entidade Music existente com os dados do MusicRequest.
     *
     * Igual ao toEntity, mas com @MappingTarget para atualizar in-place.
     * O campo 'id' é ignorado para não sobrescrever a PK.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlist", source = "playlistId", qualifiedByName = "playlistIdToPlaylist")
    void updateEntityFromRequest(MusicRequest request, @MappingTarget Music music);

    /**
     * Método auxiliar: converte um Long (playlistId) em um objeto Playlist
     * com apenas o id preenchido. O Hibernate reconhece essa referência como
     * um proxy gerenciado, equivalente a uma referência válida no banco.
     *
     * @Named("playlistIdToPlaylist") — identificador usado em @Mapping(qualifiedByName=...)
     */
    @Named("playlistIdToPlaylist")
    default Playlist playlistIdToPlaylist(Long playlistId) {
        if (playlistId == null) return null;
        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        return playlist;
    }
}
