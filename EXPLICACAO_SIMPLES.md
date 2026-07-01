# SoundList explicado sem termos técnicos

Pense neste projeto como o **funcionamento de um restaurante**. Vou usar essa analogia o tempo todo, porque a lógica é a mesma: alguém pede algo, alguém anota o pedido, alguém busca na cozinha, alguém prepara o prato, e o cliente recebe a resposta.

---

## 1. O que o professor pediu (em palavras simples)

Ele pediu para construir o "motor" (a parte de trás, invisível ao usuário final) de um aplicativo de playlists de música, parecido com o Spotify, só que bem mais simples — só a parte que **guarda e organiza os dados**, sem tela bonita, sem player de música tocando.

Esse motor precisa saber fazer 5 coisas com **playlists** e 5 coisas com **músicas**:

| Com playlists, o sistema precisa... | Com músicas, o sistema precisa... |
|---|---|
| Criar uma nova playlist | Adicionar uma música a uma playlist |
| Listar todas as playlists | Listar todas as músicas |
| Buscar uma playlist específica | Buscar uma música específica |
| Atualizar os dados de uma playlist | Atualizar os dados de uma música |
| Apagar uma playlist (e suas músicas juntas) | Apagar uma música |

Além disso, ele pediu **regras de organização e segurança** para o código não virar bagunça:

1. **Separar responsabilidades** — cada parte do código faz só uma coisa, ninguém faz "um pouco de tudo".
2. **Validar os dados** — não deixar o usuário criar uma música sem título, ou com duração negativa, por exemplo.
3. **Avisar direito quando algo dá errado** — se o cliente pedir uma playlist que não existe, o sistema não pode travar; tem que responder educadamente "essa playlist não existe".
4. **Organizar listas grandes em páginas** — se existirem 10.000 músicas, não devolver tudo de uma vez; devolver "página por página", tipo resultado de busca do Google.
5. **Nunca expor a "engrenagem interna" do banco de dados direto pro cliente** — o cliente só vê uma versão "arrumada" da informação.

É exatamente isso que o projeto entrega — e eu já testei rodando a aplicação de verdade e bati em cada uma dessas funcionalidades, todas funcionando.

---

## 2. A analogia do restaurante (guarde isso — vai te ajudar na apresentação)

| No restaurante | No projeto | Arquivo |
|---|---|---|
| **Garçom** — recebe o pedido do cliente, confere se está completo, leva para a cozinha, traz o prato pronto | **Controller** | `MusicController.java`, `PlaylistController.java` |
| **Chef de cozinha** — decide como o prato é preparado, segue as regras da casa | **Service** | `MusicService.java`, `PlaylistService.java` |
| **Despensa/estoque** — onde os ingredientes (dados) realmente ficam guardados | **Repository** + **Banco de dados** | `MusicRepository.java`, `PlaylistRepository.java` |
| **Cardápio impresso que o cliente lê** — uma versão simplificada do prato, não a receita inteira da cozinha | **DTO (Response)** | `MusicResponse.java`, `PlaylistResponse.java` |
| **Comanda que o cliente preenche** — o que ele pede, em formato padronizado | **DTO (Request)** | `MusicRequest.java`, `PlaylistRequest.java` |
| **Tradutor entre comanda e prato real** — converte "o que o cliente pediu" em "o que vai pra cozinha" e vice-versa | **Mapper** | `MusicMapper.java`, `PlaylistMapper.java` |
| **Recepcionista que pede desculpas educadamente quando algo dá errado** | **Tratamento de erros** | `GlobalExceptionHandler.java` |

A regra de ouro do restaurante: **o garçom nunca entra na despensa sozinho**. Ele sempre passa pelo chef. Isso é a regra "Controller não acessa Repository direto" — toda decisão passa pelo Service (o chef).

---

## 3. Explicando cada arquivo, um por um

### 3.1 `Music.java` e `Playlist.java` (pasta `model`) — "as fichas de cadastro"

Pense nisso como **a ficha de cadastro** de cada coisa que existe no sistema.

- **Playlist** tem: um número (id), um nome (obrigatório) e uma descrição (opcional).
- **Música** tem: um número (id), título (obrigatório), artista (obrigatório), gênero (opcional), duração em segundos (obrigatório, tem que ser positivo) e a qual playlist ela pertence.

É literalmente o "molde" de como cada coisa é guardada no banco de dados — equivalente a desenhar as colunas de uma planilha do Excel: "aqui vai o nome", "aqui vai a duração", etc.

**O detalhe importante aqui**: uma playlist "é dona" de várias músicas. Se eu apago a playlist inteira, as músicas dela são apagadas junto automaticamente — é como jogar fora uma pasta de arquivos: tudo que está dentro dela some também. Isso é configurado uma única vez no código e o sistema cuida sozinho, sem eu precisar apagar música por música manualmente.

---

### 3.2 `MusicRequest.java` e `PlaylistRequest.java` (pasta `dto`) — "a comanda do pedido"

É o formato que o cliente (quem está usando o app) **precisa preencher** para criar ou editar algo.

Por exemplo, para criar uma música, a "comanda" pede:
- Título (não pode ficar em branco)
- Artista (não pode ficar em branco)
- Gênero (pode deixar em branco, é opcional)
- Duração em segundos (tem que ser um número, e maior que zero — não existe música de duração negativa ou zero)
- A qual playlist ela pertence (obrigatório)

Se o cliente esquecer de preencher algo obrigatório, o sistema **recusa o pedido antes mesmo de tentar processá-lo** e explica exatamente o que está faltando. Isso é a "validação" — é tipo um formulário online que fica vermelho avisando "este campo é obrigatório" antes de deixar você enviar.

---

### 3.3 `MusicResponse.java` e `PlaylistResponse.java` (pasta `dto`) — "o cardápio impresso"

É o formato que o sistema **devolve** para quem pediu a informação.

Importante: a `PlaylistResponse` mostra a lista de músicas dela junto — assim quando você busca uma playlist, já vem com as músicas dela, num único pacote, sem precisar fazer uma segunda pergunta ao sistema.

Por que não devolver a "ficha de cadastro" (model) direto? Porque a ficha de cadastro tem "fios soltos" internos (a playlist aponta pra suas músicas, e cada música aponta de volta pra playlist) — se isso fosse devolvido cru, o sistema ficaria preso num loop infinito tentando montar a resposta (playlist → músicas → playlist → músicas...). O Response é uma versão "limpa", sem esses fios soltos, pronta pra ser entendida por quem está do outro lado.

---

### 3.4 `MusicRepository.java` e `PlaylistRepository.java` (pasta `repository`) — "o estoquista"

É a parte que **fala diretamente com o banco de dados**. Pega o que está guardado, guarda coisa nova, apaga, atualiza.

A parte interessante: eu **não precisei escrever** o código de "como buscar", "como salvar", "como apagar" — existe uma ferramenta (Spring Data JPA) que já entrega isso pronto, só de eu dizer "isso aqui é um repositório de Músicas" e "isso aqui é um repositório de Playlists". É como contratar um estoquista que já sabe organizar qualquer almoxarifado, sem precisar te ensinar o trabalho dele.

---

### 3.5 `MusicService.java` e `PlaylistService.java` (pasta `service`) — "o chef, dono das regras"

Aqui é onde **as decisões de negócio acontecem**. Por exemplo:

- Antes de criar uma música, o Service confere: *"a playlist que essa música quer entrar realmente existe?"* Se não existir, ele recusa e avisa.
- Ao apagar uma playlist, confere se ela existe antes de tentar apagar (senão você teria uma mensagem de erro confusa do banco de dados, em vez de uma mensagem clara tipo "essa playlist não existe").
- Ao atualizar, busca o item existente, troca os dados antigos pelos novos, e salva de volta.

O Service é o **único lugar autorizado a conversar com o estoquista (Repository)**. Nem o garçom (Controller), nem ninguém de fora, fala direto com o estoque — tudo passa pelo chef.

---

### 3.6 `MusicController.java` e `PlaylistController.java` (pasta `controller`) — "o garçom"

É a porta de entrada da aplicação — onde os pedidos chegam pela internet (tecnicamente, "endpoints").

O trabalho do garçom é simples e limitado de propósito:
1. Recebe o pedido (a "comanda" — o Request).
2. Confere rapidamente se a comanda está preenchida direito (aciona a validação).
3. Leva pro chef (Service) decidir o que fazer.
4. Devolve a resposta pronta pro cliente, com o "carimbo" certo dizendo o que aconteceu:
   - **201** = "Criado com sucesso!"
   - **200** = "Aqui está o que você pediu"
   - **204** = "Feito, apaguei, não tenho mais nada a te mostrar"
   - **400** = "Seu pedido está incompleto/errado"
   - **404** = "Isso que você pediu não existe"

O garçom **nunca decide nada sozinho** e **nunca entra na despensa**. Ele só recebe, repassa e devolve.

---

### 3.7 `MusicMapper.java` e `PlaylistMapper.java` (pasta `mapper`) — "o tradutor"

Lembra que a "comanda" (Request) e a "ficha de cadastro" (Model/Entity) têm formatos diferentes? O Mapper é quem **traduz de um formato para o outro**, automaticamente.

Por exemplo: na comanda de uma música, o cliente só escreve o **número** da playlist (`playlistId: 1`). Mas a ficha de cadastro da música precisa guardar a **playlist inteira** lá dentro (não só o número). O Mapper sabe fazer essa conversão sozinho — eu só preciso indicar a regra uma vez, e ele aplica sempre que necessário, sem eu escrever esse código de conversão toda vez na mão.

Isso evita um problema comum: copiar e colar a mesma lógica de "transformar isso naquilo" espalhada em vários lugares do código, o que deixa tudo bagunçado e fácil de gerar erro.

---

### 3.8 `GlobalExceptionHandler.java`, `ResourceNotFoundException.java`, `ErrorResponse.java` (pasta `exception`) — "a recepção de reclamações"

Esse é o "plano B" de tudo: **o que fazer quando algo dá errado**.

Existem três tipos de "deu errado" no sistema:

1. **"Isso não existe"** (404) — o cliente pediu a playlist número 999, mas só existem 3 playlists cadastradas. O sistema não trava, ele responde educadamente: *"Playlist com id 999 não encontrada"*.

2. **"Seu pedido está mal preenchido"** (400) — o cliente tentou criar uma música sem título e com duração negativa. O sistema lista **exatamente quais campos** estão errados e por quê, pra a pessoa conseguir corrigir.

3. **"Algo inesperado quebrou"** (500) — qualquer erro que eu não previ. Em vez de a aplicação travar feio (uma tela de erro genérica e confusa), ela ainda assim responde de forma organizada, avisando que algo deu errado.

A parte legal: esse "plano B" está **centralizado em um único lugar**, não espalhado em cada pedacinho do código. É como ter uma única central de atendimento ao cliente para toda a empresa, em vez de cada departamento inventar sua própria forma de lidar com reclamação.

---

### 3.9 Paginação (presente nos Controllers/Services) — "resultado de busca em páginas"

Quando você pede a lista de todas as músicas, o sistema **não devolve tudo de uma vez**. Ele devolve "página por página", como uma busca no Google que mostra 10 resultados por vez, com um botão de "próxima página".

Você consegue pedir: "me dê a página 0, com 5 itens, ordenados pelo nome" — e o sistema obedece. Isso evita travar o sistema (ou o celular de quem está usando o app) se algum dia existirem milhares de músicas cadastradas.

---

### 3.10 `application.properties` e `data.sql` — "as configurações da casa e o estoque inicial"

- `application.properties`: são os ajustes gerais da aplicação — qual banco de dados usar, em qual porta o sistema vai "atender" (8080), etc. É como o quadro de regras da cozinha pendurado na parede.
- `data.sql`: são alguns dados de exemplo (playlists e músicas) que já vêm cadastrados quando o sistema liga, só para a demonstração começar com conteúdo pronto pra mostrar, em vez de uma tela vazia.

---

## 4. O fluxo completo, do início ao fim (decore esse exemplo)

**Exemplo: alguém quer adicionar a música "Back in Black" na playlist "Rock Clássico".**

1. O app do cliente manda o pedido pra internet: *"quero criar uma música com esses dados, na playlist número 1"*.
2. O **Controller** (garçom) recebe o pedido e confere: tem título? tem artista? a duração é um número positivo? Se faltar algo, já devolve um aviso de erro (400) e para por aqui.
3. Se está tudo certo, o Controller passa o pedido pro **Service** (chef).
4. O Service confere: *"a playlist número 1 existe mesmo?"* Se não existir, devolve erro (404) e para por aqui.
5. Se existir, o Service usa o **Mapper** (tradutor) para transformar o "pedido" em uma "ficha de cadastro" completa.
6. O Service manda essa ficha pro **Repository** (estoquista), que guarda no banco de dados de verdade.
7. O banco devolve a ficha já com um número de identificação (id) novo.
8. O Mapper traduz essa ficha de volta para um formato "limpo" de resposta.
9. O Controller devolve essa resposta pro cliente, com o carimbo **201 — Criado com sucesso**.

Esse mesmo fluxo se repete (com pequenas variações) para todas as outras ações: listar, buscar, atualizar e apagar.

---

## 5. Frase-resumo para usar na apresentação

> "O sistema é organizado como um restaurante: o Controller é o garçom que só recebe e entrega pedidos, o Service é o chef que decide as regras, o Repository é quem mexe no estoque (banco de dados), e os DTOs são as comandas e os cardápios — versões simplificadas dos dados que circulam entre o cliente e o sistema, sem expor a 'receita interna'. Tudo que pode dar errado — playlist inexistente, dados mal preenchidos, erro inesperado — é tratado em um único lugar, de forma organizada, e sempre devolve uma explicação clara para quem fez o pedido."

---

## 6. Conectando com o roteiro técnico

Este documento te dá a explicação em "português simples". Para as perguntas técnicas específicas (nomes de anotações, termos como `@Transactional`, `cascade`, `MapStruct` etc.), use o [ROTEIRO_APRESENTACAO.md](ROTEIRO_APRESENTACAO.md) que já preparamos — ele tem a versão técnica das mesmas explicações, caso o professor peça os termos certos.
