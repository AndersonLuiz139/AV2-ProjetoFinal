package com.soundlist.controller;

import com.soundlist.dto.PlaylistRequest;
import com.soundlist.dto.PlaylistResponse;
import com.soundlist.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para o recurso Playlist.
 *
 * Responsabilidades desta camada (e SOMENTE estas):
 *  1. Receber a requisição HTTP e extrair parâmetros
 *  2. Validar o corpo da requisição com @Valid (aciona Bean Validation no DTO)
 *  3. Delegar ao PlaylistService (NUNCA acessa Repository diretamente)
 *  4. Retornar a resposta com o status HTTP correto
 *
 * O Controller NÃO contém regras de negócio — elas ficam no Service.
 * O Controller NÃO converte entidades — isso é responsabilidade do Mapper.
 */
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * POST /api/playlists
     * Cria uma nova playlist.
     *
     * @Valid: aciona o Bean Validation no PlaylistRequest.
     *         Se algum campo falhar, lança MethodArgumentNotValidException
     *         capturada pelo GlobalExceptionHandler → HTTP 400.
     *
     * ResponseEntity.status(201).body(...): retorna HTTP 201 Created com o
     * recurso criado no corpo da resposta.
     */
    @PostMapping
    public ResponseEntity<PlaylistResponse> create(@RequestBody @Valid PlaylistRequest request) {
        PlaylistResponse response = playlistService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/playlists?page=0&size=10&sort=name,asc
     * Lista todas as playlists com paginação.
     *
     * Pageable: Spring resolve automaticamente a partir dos query params
     *   page  → número da página (base 0)
     *   size  → quantidade de itens por página
     *   sort  → campo,direção (ex: name,asc)
     */
    @GetMapping
    public ResponseEntity<Page<PlaylistResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(playlistService.findAll(pageable));
    }

    /**
     * GET /api/playlists/{id}
     * Busca uma playlist por id. Retorna 404 se não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.findById(id));
    }

    /**
     * PUT /api/playlists/{id}
     * Atualiza os dados de uma playlist existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid PlaylistRequest request) {

        return ResponseEntity.ok(playlistService.update(id, request));
    }

    /**
     * DELETE /api/playlists/{id}
     * Remove uma playlist e todas as suas músicas (cascata).
     * Retorna HTTP 204 No Content (sem corpo na resposta).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
