package com.soundlist.controller;

import com.soundlist.dto.MusicRequest;
import com.soundlist.dto.MusicResponse;
import com.soundlist.service.MusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para o recurso Music.
 *
 * Segue os mesmos princípios do PlaylistController:
 *  - Recebe DTOs, delega ao Service, retorna DTOs
 *  - Não acessa Repository nem aplica regras de negócio
 *  - Usa @Valid para acionar Bean Validation nos requests
 */
@RestController
@RequestMapping("/api/musics")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /**
     * POST /api/musics
     * Adiciona uma música a uma playlist existente.
     * Retorna HTTP 201 Created com a música criada no corpo.
     */
    @PostMapping
    public ResponseEntity<MusicResponse> create(@RequestBody @Valid MusicRequest request) {
        MusicResponse response = musicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/musics?page=0&size=10&sort=title,asc
     * Lista todas as músicas com suporte a paginação e ordenação.
     */
    @GetMapping
    public ResponseEntity<Page<MusicResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(musicService.findAll(pageable));
    }

    /**
     * GET /api/musics/{id}
     * Busca uma música por id. Retorna 404 se não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MusicResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.findById(id));
    }

    /**
     * PUT /api/musics/{id}
     * Atualiza os dados de uma música existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MusicResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid MusicRequest request) {

        return ResponseEntity.ok(musicService.update(id, request));
    }

    /**
     * DELETE /api/musics/{id}
     * Remove uma música pelo id.
     * Retorna HTTP 204 No Content (sem corpo na resposta).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        musicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
