-- =============================================================================
-- SoundList — dados de exemplo carregados ao iniciar a aplicação
-- Compatível com H2 em memória (spring.sql.init.mode=always)
-- =============================================================================

-- Playlists de exemplo
INSERT INTO playlist (name, description) VALUES
    ('Rock Clássico', 'Os maiores clássicos do rock de todos os tempos'),
    ('Pop Brasil', 'Hits do pop brasileiro dos anos 2000'),
    ('Lo-fi Estudos', 'Músicas relaxantes para focar nos estudos');

-- Músicas de exemplo vinculadas às playlists acima
-- playlist_id = 1 → Rock Clássico
INSERT INTO music (title, artist, genre, duration, playlist_id) VALUES
    ('Bohemian Rhapsody', 'Queen',          'Rock',       354, 1),
    ('Hotel California',  'Eagles',         'Rock',       391, 1),
    ('Stairway to Heaven','Led Zeppelin',   'Rock',       480, 1),
    ('Smells Like Teen Spirit', 'Nirvana',  'Grunge',     301, 1);

-- playlist_id = 2 → Pop Brasil
INSERT INTO music (title, artist, genre, duration, playlist_id) VALUES
    ('Pais e Filhos',      'Legião Urbana',  'Rock Nacional', 276, 2),
    ('Ainda Bem',          'Marisa Monte',   'Pop',           218, 2),
    ('Aquele 1-9',         'Skank',          'Pop Rock',      195, 2);

-- playlist_id = 3 → Lo-fi Estudos
INSERT INTO music (title, artist, genre, duration, playlist_id) VALUES
    ('Coffee Shop Vibes',  'ChillHop Music', 'Lo-fi',     183, 3),
    ('Study With Me',      'Lofi Girl',      'Lo-fi',     212, 3);
