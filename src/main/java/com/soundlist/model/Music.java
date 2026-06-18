package com.soundlist.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA que representa uma Música pertencente a uma Playlist.
 *
 * O lado DONO do relacionamento bidirecional está aqui: a coluna playlist_id
 * existe na tabela music e é gerenciada pelo Hibernate via @ManyToOne.
 */
@Entity
@Table(name = "music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título da música — obrigatório. */
    @Column(nullable = false)
    private String title;

    /** Nome do artista — obrigatório. */
    @Column(nullable = false)
    private String artist;

    /** Gênero musical — opcional. */
    @Column
    private String genre;

    /**
     * Duração em segundos — obrigatório e deve ser positivo.
     * A validação de negócio fica no DTO (Bean Validation);
     * aqui apenas garantimos que o banco não aceite nulos.
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Referência à playlist dona desta música.
     *
     * @ManyToOne com FetchType.LAZY: o objeto Playlist NÃO é carregado
     * automaticamente junto com Music — só quando explicitamente acessado.
     * Isso evita queries desnecessárias (N+1).
     *
     * @JoinColumn: nomeia explicitamente a coluna FK na tabela music.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;
}
