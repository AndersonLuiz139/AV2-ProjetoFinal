package com.soundlist.mapper;

import com.soundlist.dto.PlaylistRequest;
import com.soundlist.dto.PlaylistResponse;
import com.soundlist.model.Playlist;
import org.mapstruct.*;

/**
 * Mapper MapStruct para conversão entre Playlist (entidade) e seus DTOs.
 *
 * componentModel = "spring": o MapStruct gera uma implementação desta interface
 * como um bean Spring (@Component), injetável via @Autowired / construtor.
 *
 * O MapStruct gera a implementação em tempo de COMPILAÇÃO (target/generated-sources),
 * não em runtime — isso garante segurança de tipos e performance.
 */
@Mapper(componentModel = "spring", uses = {MusicMapper.class})
public interface PlaylistMapper {

    /**
     * Converte PlaylistRequest (DTO de entrada) → Playlist (entidade JPA).
     *
     * O campo 'id' não vem do request (é gerado pelo banco), então é ignorado.
     * O campo 'musics' também é ignorado aqui — músicas são gerenciadas
     * separadamente pelo MusicService.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "musics", ignore = true)
    Playlist toEntity(PlaylistRequest request);

    /**
     * Converte Playlist (entidade JPA) → PlaylistResponse (DTO de saída).
     *
     * O MapStruct mapeia automaticamente os campos de mesmo nome (id, name, description).
     * Para 'musics', usa o MusicMapper (declarado em 'uses') para converter
     * cada Music em MusicResponse.
     */
    PlaylistResponse toResponse(Playlist playlist);

    /**
     * Atualiza uma entidade Playlist existente com os dados de um PlaylistRequest.
     *
     * @MappingTarget indica que o parâmetro 'playlist' é o objeto a ser atualizado,
     * não um objeto de retorno. Isso evita criar uma nova instância desnecessariamente.
     *
     * 'id' e 'musics' são ignorados para não sobrescrever a chave primária
     * nem a lista de músicas na atualização.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "musics", ignore = true)
    void updateEntityFromRequest(PlaylistRequest request, @MappingTarget Playlist playlist);
}
