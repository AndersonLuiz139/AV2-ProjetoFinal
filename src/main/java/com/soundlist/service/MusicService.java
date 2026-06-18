package com.soundlist.service;

import com.soundlist.dto.MusicRequest;
import com.soundlist.dto.MusicResponse;
import com.soundlist.exception.ResourceNotFoundException;
import com.soundlist.mapper.MusicMapper;
import com.soundlist.model.Music;
import com.soundlist.repository.MusicRepository;
import com.soundlist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsável pelas regras de negócio relacionadas a Music.
 *
 * Validação importante nesta camada:
 *  Além das validações Bean Validation no DTO (title, artist, duration),
 *  este Service valida se o playlistId fornecido existe no banco.
 *  Isso é uma REGRA DE NEGÓCIO, não apenas validação de formato.
 */
@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    // PlaylistRepository é necessário para validar a existência do playlistId
    private final PlaylistRepository playlistRepository;
    private final MusicMapper musicMapper;

    /**
     * Adiciona uma nova música a uma playlist existente.
     *
     * Validação de negócio: verifica se a playlist existe ANTES de criar a música.
     * Se não existir, lança ResourceNotFoundException → HTTP 404.
     */
    @Transactional
    public MusicResponse create(MusicRequest request) {
        // Regra de negócio: playlist deve existir no banco
        validatePlaylistExists(request.playlistId());

        // Converte DTO → entidade via MapStruct (inclui montar a referência Playlist)
        Music music = musicMapper.toEntity(request);

        Music saved = musicRepository.save(music);
        return musicMapper.toResponse(saved);
    }

    /**
     * Retorna todas as músicas de forma paginada.
     *
     * @Transactional(readOnly = true): Hibernate não faz dirty-checking,
     * melhorando performance em consultas.
     */
    @Transactional(readOnly = true)
    public Page<MusicResponse> findAll(Pageable pageable) {
        return musicRepository.findAll(pageable)
                .map(musicMapper::toResponse);
    }

    /**
     * Busca uma música por id. Retorna 404 se não encontrada.
     */
    @Transactional(readOnly = true)
    public MusicResponse findById(Long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Música com id " + id + " não encontrada"));

        return musicMapper.toResponse(music);
    }

    /**
     * Atualiza uma música existente.
     *
     * Valida a existência da música E da nova playlist informada no request,
     * garantindo consistência de dados em ambos os casos.
     */
    @Transactional
    public MusicResponse update(Long id, MusicRequest request) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Música com id " + id + " não encontrada"));

        // Valida se a playlist de destino existe (pode ser diferente da original)
        validatePlaylistExists(request.playlistId());

        // Atualiza a entidade existente via MapStruct (sem conversão manual)
        musicMapper.updateEntityFromRequest(request, music);

        Music updated = musicRepository.save(music);
        return musicMapper.toResponse(updated);
    }

    /**
     * Remove uma música pelo id. Retorna 404 se não encontrada.
     */
    @Transactional
    public void delete(Long id) {
        if (!musicRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Música com id " + id + " não encontrada");
        }
        musicRepository.deleteById(id);
    }

    /**
     * Método auxiliar privado: valida que um playlistId existe no banco.
     *
     * Centraliza a validação de negócio para evitar duplicação entre create() e update().
     * Lança ResourceNotFoundException (→ 404) se a playlist não for encontrada.
     */
    private void validatePlaylistExists(Long playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new ResourceNotFoundException(
                    "Playlist com id " + playlistId + " não encontrada");
        }
    }
}
