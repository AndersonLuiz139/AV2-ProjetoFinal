package com.soundlist.repository;

import com.soundlist.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para Music.
 *
 * JpaRepository já fornece findAll(Pageable) que será utilizado
 * pelo MusicService para retornar músicas paginadas.
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
}
