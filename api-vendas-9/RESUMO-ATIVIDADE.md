# Resumo da atividade – fornecedores-service

Projeto base: `api-vendas-9` (o mesmo feito em sala). Novo microsserviço: **fornecedores-service**, porta **8084**.

Legenda: ✅ feito e testado por mim | 🟡 feito, falta você | 👤 só você pode fazer

## Arquivos criados/alterados
| Arquivo | O que é |
|---|---|
| `fornecedores-service/` | Novo serviço (cópia do `clientes-service` adaptada) |
| `config-repo/fornecedores-service.properties` | Porta, H2 e Eureka |
| `config-repo/fornecedores-service-docker.properties` | Eureka apontando para `eureka-server` |
| `docker-compose.yml` | Bloco `fornecedores-service` adicionado |
| `.github/workflows/fornecedores-service.yml` | Pipeline do GitHub Actions |
| `RESUMO-ATIVIDADE.md` | Este arquivo |
| `prints/` e `Atividade-Prints.docx` | Evidências (prints e PDF) |

## Exercício a exercício

**Ex. 1 – Fork, clone, Eureka + produtos-service** 👤  
Fork e clone são seus. Testei que eureka-server e produtos-service sobem e o produtos aparece em `localhost:8761`.

**Ex. 2 – Branch `atividade-seunome`, readme, PR** 👤  
Não mexi no `readme.md` nem em Git. Você: criar a branch, acrescentar seu nome completo e matrícula ao `readme.md`, commit, push e abrir o Pull Request para a `main` do seu fork.

**Ex. 3 – Novo serviço a partir do clientes-service** ✅  
Copiei a pasta e troquei: `artifactId`/`name` (`fornecedores-service`), pacote `com.exemplo.fornecedoresservice`, classe `FornecedorServiceApplication`, `spring.application.name=fornecedores-service`, porta 8084. Sobe sem erro.

**Ex. 4 – Entidade, repositório, carga inicial** ✅  
`Fornecedor` (id gerado, `nome` obrigatório, `cnpj` obrigatório e único), `FornecedorRepository` e `DataInitializer` que cadastra 5 fornecedores ao subir. H2 Console: `http://localhost:8084/h2-console`, JDBC URL `jdbc:h2:mem:fornecedoresdb`, usuário `sa`, senha vazia, consulta `SELECT * FROM FORNECEDOR`.

**Ex. 5 – Service e controller (GET lista e GET por id)** ✅  
`FornecedorService` e `FornecedorController`. `GET /fornecedores` devolve 200 com a lista; `GET /fornecedores/{id}` devolve 404 quando não existe (mesmo padrão do `ProdutoController`). Teste do 404: `/fornecedores/999`.

**Ex. 6 – Registro no Eureka** ✅  
Dependência `spring-cloud-starter-netflix-eureka-client` (já veio do clientes) + `@EnableDiscoveryClient` + propriedades `eureka.client.*` (agora no config-repo). `FORNECEDORES-SERVICE` aparece em `localhost:8761`.

**Ex. 7 – Config Server** ✅  
Porta, H2 e Eureka foram para `config-repo/fornecedores-service.properties`. O `application.properties` do serviço tem só o nome e `spring.config.import=optional:configserver:http://localhost:8888`. Teste: `http://localhost:8888/fornecedores-service/default`. O log de subida mostra "Tomcat started on port 8084" vindo do Config Server.

**Ex. 8 – Acesso pelo gateway (8085)** ✅ com atenção  
Sem rota escrita; o gateway descobre pelo Eureka. URL: `http://localhost:8085/fornecedores-service/fornecedores`.  
⚠️ O gateway da sala tem um filtro JWT (`TokenFilter`): sem header `Authorization: Bearer <token>` responde 401. Para o print, use um token válido do `auth-service` (login em `/auth-service/usuarios/login`). Eu usei um token de teste assinado com o mesmo segredo.

**Ex. 9 – POST /fornecedores** ✅  
Recebe JSON, salva e devolve **201** com o objeto (com `id`). Exemplo:
```
curl -i -X POST localhost:8084/fornecedores -H "Content-Type: application/json" -d '{"nome":"Novo Forn","cnpj":"99.888.777/0001-66"}'
```

**Ex. 10 – Feign para o produtos-service** ✅  
Dependência `spring-cloud-starter-openfeign`, `@EnableFeignClients`, interface `ProdutoClient` (`@FeignClient(name="produtos-service")`), `ProdutoDTO` e endpoint `GET /fornecedores/produtos`, que devolve os 10 produtos.

**Ex. 11 – Docker** ✅  
Testado: build e subida com Docker (eureka, config-server, produtos, fornecedores e gateway). Dockerfile (porta 8084), perfil docker no config-repo e bloco no `docker-compose.yml`. ⚠️ O `auth-service` já publica a 8084 no host, então o meu foi publicado como `8086:8084`. Pelo gateway (8085) não muda nada.

**Ex. 12 – GitHub Actions** 🟡  
`.github/workflows/fornecedores-service.yml`: a cada push faz checkout, instala Java 17 e roda `mvn -B package` em `fornecedores-service`. Depende do seu commit/push; o print do check verde é da aba Actions.

## Observações importantes
- **A pasta `.github/workflows` precisa estar na raiz do repositório** (a pasta `api-vendas-9` do fork), senão o GitHub não executa.
- **Antes do commit**, garanta que estes itens estão no seu clone: `fornecedores-service/`, os 2 arquivos novos do `config-repo`, `docker-compose.yml` e o workflow.
- **Commits são seus** (pedido seu). Todos na branch `atividade-seunome`.
- O `auth-service` não compilou no meu ambiente (JDK 24 + Lombok); com Java 17 deve compilar. Não alterei nada nele.
- Pastas `target/` são ignoradas pelo `.gitignore`.

## Docker – o que aconteceu no meu teste
- As imagens construíram e o `fornecedores-service` respondeu pelo gateway (8085) dentro do Docker.
- No `docker compose up` completo, o **auth-service** não subiu aqui porque existe um container antigo com o mesmo nome (`auth-service`, de outro projeto, parado). Não apaguei. Se der o mesmo erro aí: `docker rm auth-service` (só se puder perder aquele container).
- **Corrida de inicialização:** o `depends_on` não espera o config-server ficar pronto, então na 1ª subida os serviços podem não achar a configuração e não registrar no Eureka. Solução: `docker compose restart produtos-service fornecedores-service gateway` depois que Eureka e Config Server estiverem no ar. Antes do print, confirme os 3 no Eureka.

## Prints
As imagens estão em `prints/` e o documento editável é **`Atividade-Prints.docx`** (Word). Já inclui o print do H2 Console (Ex. 4). Faltam só os quadros "COLE SEU PRINT AQUI": **Ex. 2 (Pull Request) e Ex. 12 (Actions)**, mais seu nome, matrícula e o link do repositório. No fim, exporte para PDF pelo Word (Arquivo > Salvar como > PDF).

## Seus passos (Git/GitHub), na ordem
```bash
# Ex. 1 – no GitHub: botão Fork no repositório do professor. Depois:
git clone https://github.com/SEU-USUARIO/NOME-DO-REPO.git
cd NOME-DO-REPO

# Ex. 2
git checkout -b atividade-seunome
# edite o readme.md e acrescente: "Nome Completo – Matrícula 123456"
git add readme.md
git commit -m "Adiciona nome e matricula"
git push -u origin atividade-seunome
# no GitHub: Compare & pull request  (base: main do SEU fork)  -> print (Ex. 2)

# Depois: copie para o clone (mesma estrutura de pastas):
#   fornecedores-service/  config-repo/fornecedores-service*.properties
#   docker-compose.yml  .github/workflows/fornecedores-service.yml
git add .
git commit -m "Adiciona fornecedores-service"
git push        # Ex. 12: o pipeline roda sozinho; aba Actions -> print do check verde
```
Observações:
- Se o clone do seu fork tiver uma pasta-raiz a mais, o `.github/workflows` deve ficar na **raiz do repositório** e o `working-directory` do workflow deve apontar para o caminho real de `fornecedores-service`.
- Para o commit sem meu nome como coautor: eu não adiciono nenhuma linha `Co-Authored-By`; se você mesmo commitar, também não vai aparecer nada.
