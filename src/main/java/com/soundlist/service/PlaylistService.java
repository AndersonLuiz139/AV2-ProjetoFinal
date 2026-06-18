package com.soundlist.service;

import com.soundlist.dto.PlaylistRequest;
import com.soundlist.dto.PlaylistResponse;
import com.soundlist.exception.ResourceNotFoundException;
import com.soundlist.mapper.PlaylistMapper;
import com.soundlist.model.Playlist;
import com.soundlist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsável pelas regras de negócio relacionadas a Playlist.
 *
 * Arquitetura em camadas:
 *  - Recebe DTOs do Controller
 *  - Chama o Repository para persistência
 *  - Usa o PlaylistMapper para toda conversão entidade <-> DTO
 *  - Nunca expõe entidades JPA para fora desta camada
 *
 * @RequiredArgsConstructor (Lombok): gera construtor com todos os campos 'final',
 * permitindo injeção de dependência via construtor (melhor prática vs @Autowired).
 */
@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;

    /**
     * Cria uma nova Playlist.
     *
     * Fluxo: Request → Mapper(toEntity) → Repository(save) → Mapper(toResponse) → Response
     *
     * @Transactional garante que toda a operação ocorra em uma única transação de banco.
     */
    @Transactional
    public PlaylistResponse create(PlaylistRequest request) {
        // Converte o DTO de entrada em entidade JPA via MapStruct (sem conversão manual)
        Playlist playlist = playlistMapper.toEntity(request);

        // Persiste no banco — o Hibernate gerencia o INSERT e retorna a entidade com id preenchido
        Playlist saved = playlistRepository.save(playlist);

        // Converte a entidade salva em DTO de saída via MapStruct
        return playlistMapper.toResponse(saved);
    }

    /**
     * Retorna todas as playlists de forma paginada.
     *
     * O Pageable vem do Controller e carrega os parâmetros page, size e sort
     * enviados pelo cliente na query string.
     *
     * .map(playlistMapper::toResponse): converte cada Playlist da página em
     * PlaylistResponse SEM materializar a lista inteira em memória.
     *
     * @Transactional(readOnly = true): otimização — o Hibernate não rastreia
     * mudanças (dirty-checking) em transações somente-leitura.
     */
    @Transactional(readOnly = true)
    public Page<PlaylistResponse> findAll(Pageable pageable) {
        return playlistRepository.findAll(pageable)
                .map(playlistMapper::toResponse);
    }

    /**
     * Busca uma Playlist por id.
     *
     * orElseThrow: se o id não existir no banco, lança ResourceNotFoundException,
     * que será capturada pelo GlobalExceptionHandler e retornará HTTP 404.
     */
    @Transactional(readOnly = true)
    public PlaylistResponse findById(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist com id " + id + " não encontrada"));

        return playlistMapper.toResponse(playlist);
    }

    /**
     * Atualiza os dados de uma Playlist existente.
     *
     * Busca a entidade (404 se não existir), usa o mapper para atualizar
     * apenas os campos do request sem criar uma nova instância da entidade.
     * O save() com entidade já gerenciada pelo Hibernate dispara um UPDATE.
     */
    @Transactional
    public PlaylistResponse update(Long id, PlaylistRequest request) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist com id " + id + " não encontrada"));

        // Atualiza a entidade existente com os novos dados via MapStruct (sem conversão manual)
        playlistMapper.updateEntityFromRequest(request, playlist);

        Playlist updated = playlistRepository.save(playlist);
        return playlistMapper.toResponse(updated);
    }

    /**
     * Remove uma Playlist e todas as suas músicas em cascata.
     *
     * A remoção em cascata é configurada na entidade Playlist via
     * cascade = CascadeType.ALL e orphanRemoval = true no @OneToMany.
     * O Hibernate cuida de deletar as Music filhas automaticamente.
     */
    @Transactional
    public void delete(Long id) {
        // Valida existência antes de deletar — garante 404 em vez de operação silenciosa
        if (!playlistRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Playlist com id " + id + " não encontrada");
        }
        playlistRepository.deleteById(id);
    }
}
