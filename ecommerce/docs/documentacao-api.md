# LinkWork – Documentação da API

URL base: `http://localhost:8080`
Formato: JSON (`Content-Type: application/json`)
Autenticação: **HTTP Basic** (obrigatória em todos os endpoints)

## 1. Autenticação e perfis

| Usuário   | Senha        | Perfil    | Permissões |
|-----------|--------------|-----------|------------|
| `admin`   | `admin123`   | `ADMIN`   | Acesso total |
| `usuario` | `usuario123` | `USUARIO` | Consulta, cadastra e atualiza (exceto áreas); **não** exclui nem bloqueia usuários |

Operações restritas ao perfil **ADMIN**:

- `DELETE` em qualquer recurso;
- `POST /area` e `PUT /area/{id}`;
- `PATCH /usuario/{id}/bloquear` e `PATCH /usuario/{id}/desbloquear`.

| Situação | Resposta |
|----------|----------|
| Sem credenciais ou senha errada | `401 Unauthorized` |
| Autenticado, mas sem o perfil exigido | `403 Forbidden` |

> Os usuários de login são de **teste** e ficam em memória (permitido pelo enunciado). A entidade `Usuario` representa os usuários do domínio (freelancers, empresas e pessoas físicas).

## 2. Padrão de erros

Todas as respostas de erro seguem o formato:

```json
{
  "dataHora": "2026-11-16T21:30:00",
  "status": 409,
  "erro": "Conflict",
  "mensagem": "O e-mail 'lucas.andrade@email.com' já está em uso",
  "caminho": "/usuario"
}
```

Erros de validação (`400`) trazem também a lista `detalhes`, com o campo e a mensagem:

```json
{
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos. Verifique os campos informados.",
  "caminho": "/usuario",
  "detalhes": [
    "documento: O documento deve conter 11 dígitos (CPF) ou 14 dígitos (CNPJ), somente números",
    "email: Informe um e-mail válido",
    "nome: O nome deve ter entre 3 e 100 caracteres"
  ]
}
```

| Código | Quando ocorre |
|--------|---------------|
| 200 OK | Consulta, atualização ou ação concluída |
| 201 Created | Registro criado (cabeçalho `Location` com a URL do recurso) |
| 204 No Content | Registro excluído |
| 400 Bad Request | Dados inválidos (Bean Validation), JSON malformado, enum inválido, parâmetro com tipo errado |
| 401 Unauthorized | Não autenticado |
| 403 Forbidden | Sem permissão |
| 404 Not Found | Recurso inexistente ou busca sem resultados |
| 409 Conflict | Violação de regra de negócio |

## 3. Relacionamentos no corpo das requisições

Entidades relacionadas são informadas apenas pelo `id`:

```json
"area": { "id": 1 },
"contratante": { "id": 1 }
```

Campos controlados pelo sistema (`id`, `status`, datas, `freelancer` do projeto, `bloqueado`) são ignorados quando enviados.

---

## 4. Áreas – `/area`

| Método | Endpoint | Descrição | Perfil |
|--------|----------|-----------|--------|
| GET | `/area` | Lista as áreas (ordem alfabética) | Todos |
| GET | `/area/{id}` | Busca uma área | Todos |
| POST | `/area` | Cadastra uma área | ADMIN |
| PUT | `/area/{id}` | Atualiza uma área | ADMIN |
| DELETE | `/area/{id}` | Exclui uma área | ADMIN |

```json
{ "nome": "Fotografia e Vídeo", "descricao": "Ensaios, edição de fotos e vídeos" }
```

Regras: nome obrigatório e único (409). **Não exclui área com projetos associados (409).**

## 5. Usuários – `/usuario`

| Método | Endpoint | Descrição | Perfil |
|--------|----------|-----------|--------|
| GET | `/usuario` | Lista os usuários | Todos |
| GET | `/usuario?tipo=FREELANCER` | **Consulta:** usuários por tipo | Todos |
| GET | `/usuario/freelancers?competencia=Java` | **Consulta:** freelancers com uma competência | Todos |
| GET | `/usuario/{id}` | Busca um usuário | Todos |
| POST | `/usuario` | Cadastra um usuário | Todos |
| PUT | `/usuario/{id}` | Atualiza um usuário | Todos |
| DELETE | `/usuario/{id}` | Exclui um usuário | ADMIN |
| PATCH | `/usuario/{id}/bloquear` | Bloqueia um usuário | ADMIN |
| PATCH | `/usuario/{id}/desbloquear` | Desbloqueia um usuário | ADMIN |

```json
{
  "nome": "Juliana Martins",
  "email": "juliana.martins@email.com",
  "documento": "55566677788",
  "tipo": "FREELANCER",
  "telefone": "(14) 99456-7890",
  "cidade": "Ourinhos",
  "competencias": "Python, Power BI, SQL"
}
```

`tipo`: `FREELANCER`, `EMPRESA` ou `PESSOA_FISICA`.

Regras:
- **RN01** – identificação válida: CPF com 11 dígitos (freelancer/pessoa física) ou CNPJ com 14 dígitos (empresa) → 400; documento único → 409.
- **RN02** – e-mail único → 409.
- Usuário com projetos, propostas ou mensagens não pode ser excluído (409) – deve ser bloqueado.
- Não é possível alterar o tipo de um usuário que já possui vínculos (409).

## 6. Projetos – `/projeto`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/projeto` | Lista os projetos (mais recentes primeiro) |
| GET | `/projeto?status=ABERTO` | **Consulta:** projetos por status |
| GET | `/projeto/busca?areaId=&cidade=&valorMin=&valorMax=&termo=` | **Consulta (JPQL):** busca e filtro de projetos abertos – todos os parâmetros são opcionais |
| GET | `/projeto/{id}` | Busca um projeto |
| GET | `/projeto/painel` · `/projeto/painel?usuarioId=3` | Painel de projetos ativos (status, prazo, dias restantes, atraso, pagamento) |
| GET | `/projeto/{id}/compatibilidade/{freelancerId}` | Compatibilidade entre competências e requisitos |
| GET | `/projeto/recomendados/{freelancerId}` | Projetos abertos mais compatíveis com o freelancer |
| POST | `/projeto` | Publica um projeto |
| PUT | `/projeto/{id}` | Edita um projeto |
| DELETE | `/projeto/{id}` | Exclui um projeto (ADMIN) |
| PATCH | `/projeto/{id}/iniciar` | Inicia o projeto |
| PATCH | `/projeto/{id}/concluir` | Conclui o projeto |
| PATCH | `/projeto/{id}/cancelar` | Cancela o projeto |

```json
{
  "titulo": "Sistema de agendamento para clínica",
  "descricao": "Sistema web para agendamento de consultas com controle de horários, pacientes e profissionais.",
  "valor": 8000.00,
  "prazo": "2027-04-30",
  "cidade": "Remoto",
  "requisitos": "Java, Spring Boot, MySQL, Docker",
  "area": { "id": 1 },
  "contratante": { "id": 1 }
}
```

Status: `ABERTO → AGUARDANDO_PAGAMENTO → EM_ANDAMENTO → FINALIZADO` (ou `CANCELADO`).

Regras:
- **RN03** – descrição com no mínimo 30 caracteres → 400.
- Somente `EMPRESA` ou `PESSOA_FISICA` (não bloqueados) publicam projetos → 409.
- **RN04** – edição apenas com status `ABERTO`; exclusão apenas `ABERTO` ou `CANCELADO` → 409.
- **RN05** – busca sem resultados retorna 404 com mensagem informativa.
- **RN06** – filtros por área, localização e valor.
- **RN07** – análise de compatibilidade (percentual, requisitos atendidos e faltantes).
- **RN10** – iniciar somente com pagamento `CONFIRMADO` → 409.
- **RN12** – concluir projeto `EM_ANDAMENTO` → `FINALIZADO`.
- **RN13** – painel exibe status e prazos.

Exemplo de resposta da compatibilidade:

```json
{
  "projetoId": 1,
  "projeto": "API REST para loja virtual",
  "freelancerId": 3,
  "freelancer": "Lucas Andrade",
  "percentual": 100,
  "requisitosAtendidos": ["Java", "Spring Boot", "MySQL"],
  "requisitosFaltantes": []
}
```

## 7. Propostas – `/proposta`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/proposta` | Lista as propostas |
| GET | `/proposta?projetoId=1` | **Consulta:** propostas de um projeto (menor valor primeiro) |
| GET | `/proposta?freelancerId=3` | **Consulta:** propostas de um freelancer |
| GET | `/proposta/{id}` | Busca uma proposta |
| POST | `/proposta` | Envia uma proposta |
| PUT | `/proposta/{id}` | Altera valor, prazo e mensagem |
| DELETE | `/proposta/{id}` | Exclui (ADMIN) |
| PATCH | `/proposta/{id}/aceitar` | **Contrata o freelancer** |
| PATCH | `/proposta/{id}/recusar` | Recusa a proposta |

```json
{
  "valor": 7800.00,
  "prazoDias": 60,
  "mensagem": "Desenvolvo o sistema de agendamento completo em 60 dias.",
  "projeto": { "id": 6 },
  "freelancer": { "id": 3 }
}
```

Regras: somente `FREELANCER` não bloqueado; projeto `ABERTO`; uma proposta por freelancer por projeto; alterar/aceitar/recusar apenas `PENDENTE`; proposta `ACEITA` não pode ser excluída (todas → 409).

Ao **aceitar**: a proposta fica `ACEITA`, as demais pendentes do projeto ficam `RECUSADA`, o freelancer é vinculado ao projeto e o projeto passa para `AGUARDANDO_PAGAMENTO`.

## 8. Mensagens – `/mensagem`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/mensagem` | Lista as mensagens |
| GET | `/mensagem/conversa?usuario1=1&usuario2=3` | **Consulta (JPQL):** histórico da conversa (RN09) |
| GET | `/mensagem/projeto/{projetoId}` | **Consulta:** mensagens de um projeto |
| GET | `/mensagem/{id}` | Busca uma mensagem |
| POST | `/mensagem` | Envia uma mensagem |
| PUT | `/mensagem/{id}` | Edita o conteúdo |
| DELETE | `/mensagem/{id}` | Exclui (ADMIN) |

```json
{
  "conteudo": "Segue o link do meu portfólio.",
  "remetente": { "id": 3 },
  "destinatario": { "id": 1 },
  "projeto": { "id": 1 }
}
```

Regras: **RN08** – usuário bloqueado não envia mensagens (409); remetente e destinatário diferentes (400); `projeto` é opcional.

## 9. Pagamentos – `/pagamento`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/pagamento` | Lista os pagamentos |
| GET | `/pagamento?status=CONFIRMADO` | **Consulta:** pagamentos por status |
| GET | `/pagamento/freelancer/{id}/total` | **Consulta (JPQL):** total recebido pelo freelancer |
| GET | `/pagamento/{id}` | Busca um pagamento |
| POST | `/pagamento` | Registra o pagamento de um projeto |
| PUT | `/pagamento/{id}` | Altera valor/forma (somente `PENDENTE`) |
| DELETE | `/pagamento/{id}` | Exclui (ADMIN) |
| PATCH | `/pagamento/{id}/confirmar` | Confirma o pagamento |

```json
{ "valor": 4800.00, "forma": "PIX", "projeto": { "id": 1 } }
```

`forma`: `PIX`, `CARTAO_CREDITO`, `CARTAO_DEBITO` ou `BOLETO`.

Regras: projeto em `AGUARDANDO_PAGAMENTO`; um pagamento por projeto; valor igual ao da proposta aceita; **RN11** – pagamento `CONFIRMADO` não pode ser excluído (todas → 409).

---

## 10. Funcionalidade específica do PI – Fluxo de contratação

| Passo | Requisição | Resultado |
|-------|------------|-----------|
| 1 | `GET /projeto/1/compatibilidade/3` | Freelancer 100% compatível |
| 2 | `PATCH /proposta/1/aceitar` | Projeto 1 → `AGUARDANDO_PAGAMENTO` |
| 3 | `PATCH /projeto/1/iniciar` | **409** – sem pagamento (RN10) |
| 4 | `POST /pagamento` `{ "valor": 4800.00, "forma": "PIX", "projeto": { "id": 1 } }` | Pagamento `PENDENTE` (id 4) |
| 5 | `PATCH /pagamento/4/confirmar` | Pagamento `CONFIRMADO` |
| 6 | `PATCH /projeto/1/iniciar` | Projeto → `EM_ANDAMENTO` |
| 7 | `GET /projeto/painel` | Painel com status e prazos |
| 8 | `PATCH /projeto/1/concluir` | Projeto → `FINALIZADO` (RN12) |
| 9 | `GET /pagamento/freelancer/3/total` | Total recebido atualizado |

## 11. Consultas personalizadas (Spring Data JPA)

| Repositório | Método | Tipo |
|-------------|--------|------|
| `ProjetoRepositorio` | `filtrar(areaId, cidade, valorMin, valorMax, termo)` | `@Query` JPQL |
| `ProjetoRepositorio` | `findByStatusOrderByPrazoAsc` / `findByStatusInOrderByPrazoAsc` | Método derivado |
| `MensagemRepositorio` | `buscarConversa(usuario1, usuario2)` | `@Query` JPQL |
| `PagamentoRepositorio` | `totalRecebidoPorFreelancer(freelancerId)` | `@Query` JPQL |
| `PagamentoRepositorio` | `findByStatusOrderByDataDesc` | Método derivado |
| `UsuarioRepositorio` | `findByTipoOrderByNome` / `findByTipoAndCompetenciasContainingIgnoreCaseOrderByNome` | Método derivado |
| `PropostaRepositorio` | `findByProjetoIdOrderByValorAsc` / `findByFreelancerIdOrderByDataEnvioDesc` | Método derivado |
