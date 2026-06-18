package com.soundlist.repository;

import com.soundlist.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para Playlist.
 *
 * JpaRepository já fornece:
 *  - save(), findById(), findAll(), findAll(Pageable), deleteById(), existsById()…
 *
 * Não é necessário código adicional para os casos de uso básicos da API.
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
