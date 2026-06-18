package com.soundlist.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa uma Playlist.
 *
 * O relacionamento @OneToMany com cascade = ALL e orphanRemoval = true garante
 * que, ao remover uma Playlist, TODAS as suas músicas são removidas em cascata
 * pelo Hibernate — sem necessidade de código manual no Service para isso.
 *
 * mappedBy = "playlist" indica que o lado DONO do relacionamento é Music.playlist
 * (onde está a coluna FK playlist_id na tabela music).
 */
@Entity
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome da playlist — obrigatório no banco. */
    @Column(nullable = false)
    private String name;

    /** Descrição opcional da playlist. */
    @Column
    private String description;

    /**
     * Lista de músicas pertencentes a esta playlist.
     *
     * cascade = CascadeType.ALL → qualquer operação (persist, merge, remove…)
     *   feita na Playlist é propagada para suas Músicas.
     *
     * orphanRemoval = true → se uma Music for removida desta lista (desassociada),
     *   ela é automaticamente deletada do banco.
     */
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Music> musics = new ArrayList<>();
}
