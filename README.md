# SoundList

API REST de gerenciamento de playlists musicais desenvolvida com Spring Boot 3.

## Tecnologias

| Tecnologia | Versão | Papel |
|---|---|---|
| Java | 17 | Linguagem |
| Spring Boot | 3.4.1 | Framework principal |
| Spring Data JPA | — | Persistência e repositórios |
| Spring Validation | — | Validação de DTOs (Bean Validation) |
| H2 | — | Banco em memória (desenvolvimento) |
| MapStruct | 1.6.3 | Mapeamento entidade ↔ DTO |
| Lombok | 1.18.42 | Redução de boilerplate |
| Maven | 3.9+ | Build e gerenciamento de dependências |

## Pré-requisitos

- Java 17+
- Maven 3.9+

## Como rodar

```bash
mvn spring-boot:run
```

Ou compilando e executando o JAR:

```bash
mvn clean package -DskipTests
java -jar target/soundlist-0.0.1-SNAPSHOT.jar
```

A aplicação sobe na porta `8080`. O banco H2 é criado em memória e populado automaticamente com dados de exemplo a cada inicialização.

## Estrutura do projeto

```
src/main/java/com/soundlist/
├── controller/          # Camada HTTP — recebe requisições, retorna respostas
│   ├── MusicController.java
│   └── PlaylistController.java
├── service/             # Regras de negócio
│   ├── MusicService.java
│   └── PlaylistService.java
├── repository/          # Acesso ao banco via Spring Data JPA
│   ├── MusicRepository.java
│   └── PlaylistRepository.java
├── model/               # Entidades JPA
│   ├── Music.java
│   └── Playlist.java
├── dto/                 # Objetos de transferência de dados
│   ├── MusicRequest.java
│   ├── MusicResponse.java
│   ├── PlaylistRequest.java
│   └── PlaylistResponse.java
├── mapper/              # Conversão entidade ↔ DTO via MapStruct
│   ├── MusicMapper.java
│   └── PlaylistMapper.java
├── exception/           # Tratamento global de erros
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ErrorResponse.java
└── SoundListApplication.java
```

## Modelo de dados

```
Playlist 1 ──── N Music
```

- Uma **Playlist** possui `id`, `name` e `description`.
- Uma **Music** possui `id`, `title`, `artist`, `genre`, `duration` (em segundos) e `playlistId`.
- A exclusão de uma Playlist remove todas as suas músicas em cascata (`CascadeType.ALL` + `orphanRemoval = true`).

## Endpoints

### Playlists — `/api/playlists`

| Método | URL | Status | Descrição |
|---|---|---|---|
| `POST` | `/api/playlists` | 201 | Cria uma nova playlist |
| `GET` | `/api/playlists` | 200 | Lista todas (paginado) |
| `GET` | `/api/playlists/{id}` | 200 / 404 | Busca por ID |
| `PUT` | `/api/playlists/{id}` | 200 / 404 | Atualiza |
| `DELETE` | `/api/playlists/{id}` | 204 / 404 | Remove (cascata nas músicas) |

**Corpo de criação/atualização:**
```json
{
  "name": "Minha Playlist",
  "description": "Descrição opcional"
}
```

**Resposta:**
```json
{
  "id": 1,
  "name": "Minha Playlist",
  "description": "Descrição opcional",
  "musics": []
}
```

---

### Músicas — `/api/musics`

| Método | URL | Status | Descrição |
|---|---|---|---|
| `POST` | `/api/musics` | 201 | Adiciona música a uma playlist |
| `GET` | `/api/musics` | 200 | Lista todas (paginado) |
| `GET` | `/api/musics/{id}` | 200 / 404 | Busca por ID |
| `PUT` | `/api/musics/{id}` | 200 / 404 | Atualiza |
| `DELETE` | `/api/musics/{id}` | 204 / 404 | Remove |

**Corpo de criação/atualização:**
```json
{
  "title": "Bohemian Rhapsody",
  "artist": "Queen",
  "genre": "Rock",
  "duration": 354,
  "playlistId": 1
}
```

**Campos obrigatórios:** `title`, `artist`, `duration` (positivo), `playlistId`.  
**Campo opcional:** `genre`.

**Resposta:**
```json
{
  "id": 1,
  "title": "Bohemian Rhapsody",
  "artist": "Queen",
  "genre": "Rock",
  "duration": 354,
  "playlistId": 1
}
```

---

### Paginação

Todos os endpoints de listagem (`GET /api/playlists` e `GET /api/musics`) aceitam os parâmetros:

| Parâmetro | Exemplo | Descrição |
|---|---|---|
| `page` | `0` | Número da página (base 0) |
| `size` | `10` | Itens por página |
| `sort` | `name,asc` | Campo e direção de ordenação |

Exemplo: `GET /api/playlists?page=0&size=5&sort=name,asc`

## Tratamento de erros

Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Playlist com id 99 não encontrada",
  "path": "/api/playlists/99"
}
```

Para erros de validação (400), é incluída a lista de campos inválidos:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos campos da requisição",
  "path": "/api/musics",
  "fieldErrors": [
    { "field": "title", "message": "O título da música é obrigatório" },
    { "field": "duration", "message": "A duração deve ser um valor positivo em segundos" }
  ]
}
```

| Status | Causa |
|---|---|
| 400 | Campos inválidos no corpo da requisição |
| 404 | Recurso não encontrado (id inexistente) |
| 500 | Erro inesperado no servidor |

## Console H2

Durante o desenvolvimento, é possível acessar o banco de dados pelo console web:

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:soundlistdb`
- **Usuário:** `sa`
- **Senha:** *(vazio)*

O banco é recriado a cada reinicialização. Os dados de exemplo são carregados automaticamente a partir de `src/main/resources/data.sql`:

- 3 playlists: *Rock Clássico*, *Pop Brasil*, *Lo-fi Estudos*
- 9 músicas distribuídas entre as playlists

## Testando a API

O arquivo `requests.http` na raiz do projeto contém 15 requisições de exemplo prontas para uso:

- **IntelliJ IDEA:** suporte nativo — abra o arquivo e clique em **Run** ao lado de cada requisição.
- **VS Code:** instale a extensão [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) e clique em **Send Request**.

## Arquitetura

O projeto segue a arquitetura em camadas com separação clara de responsabilidades:

```
Controller → Service → Repository → Banco
     ↕            ↕
    DTO        Mapper ↔ Entidade
```

- **Controller:** recebe HTTP, valida com `@Valid`, delega ao Service.
- **Service:** aplica regras de negócio, gerencia transações com `@Transactional`.
- **Repository:** acesso ao banco via Spring Data JPA (sem SQL manual).
- **Mapper (MapStruct):** converte entidades em DTOs e vice-versa em tempo de compilação.
- **DTOs:** entidades JPA nunca são expostas diretamente pela API.