# iCompras — Microservices com Spring Boot e Kafka

Projeto desenvolvido durante o curso **“Aprenda Microservices com Spring Boot, Apache Kafka, Docker, Webhooks, MinIO e Jasper Reports na prática”**. O objetivo é exercitar uma arquitetura distribuída de comércio eletrônico: serviços independentes cuidam de partes do fluxo de compra, comunicando-se por APIs HTTP quando precisam de uma resposta imediata e por eventos Kafka para etapas assíncronas.

O exemplo reúne cadastro de produtos e clientes, criação e pagamento de pedidos, emissão de nota fiscal e logística. Também demonstra persistência isolada por serviço, integração com armazenamento compatível com S3 e geração de PDF com JasperReports.

## Tecnologias e dependências

- **Java 21**, **Maven** e **Spring Boot** (as versões dos pais Maven variam entre módulos: 3.5.0 e 3.5.3).
- **Spring Web** para APIs REST; **Spring Data JPA/Hibernate** e driver **PostgreSQL** nos serviços com persistência.
- **Spring Kafka** para publicação e consumo de mensagens; **Spring Cloud OpenFeign** em `pedidos` para chamadas HTTP aos serviços de clientes e produtos.
- **MapStruct** para mapeamento de objetos em `pedidos` e **Lombok** para reduzir código repetitivo.
- **JasperReports 7.0.7** para gerar nota fiscal em PDF.
- **MinIO Java SDK 8.5.17** para integração com armazenamento S3. O compose atual em `icompras-servicos/bucket` inicia **RustFS**, compatível com S3, e não MinIO.
- **Docker Compose** para executar PostgreSQL, Kafka/Zookeeper, Kafka UI e armazenamento de objetos.

Cada diretório de serviço tem seu próprio `pom.xml` e pode ser compilado de forma independente com Maven. As dependências específicas estão declaradas nesses arquivos.

## Estrutura do repositório

| Diretório | Responsabilidade |
| --- | --- |
| `produtos/` | API e persistência do catálogo de produtos. |
| `clientes/` | API e persistência de clientes. |
| `pedidos/` | Recebe pedidos, consulta produtos/clientes, solicita pagamento e acompanha o estado do pedido. |
| `faturamento/faturamento/` | Consome pedidos pagos, gera nota fiscal PDF, armazena o arquivo e publica a atualização de faturamento. |
| `logistica/` | Consome pedidos faturados, prepara o envio, gera código de rastreio e publica a atualização logística. |
| `icompras-servicos/` | **Não é um microserviço de negócio.** É uma pasta de apoio que reúne os Docker Compose dos serviços externos, scripts SQL e demais recursos locais de infraestrutura. |

A pasta `icompras-servicos` ajuda a localizar os recursos compartilhados do projeto: `broker/` contém o compose de Kafka, Zookeeper e Kafka UI; `bucket/` contém o compose do armazenamento S3 compatível; e `database/` contém o compose do PostgreSQL e os scripts `postgres-schema.sql` e `migration.sql`.

## Como os serviços se comunicam

O fluxo principal combina chamadas síncronas e eventos assíncronos:

1. O cliente cria um pedido pela API de `pedidos` (`POST /pedidos`). O serviço valida os dados e usa HTTP/Feign para consultar `clientes` e `produtos` quando necessário.
2. `pedidos` registra o pedido e solicita o pagamento ao serviço bancário configurado como cliente externo. O retorno do pagamento chega por callback HTTP em `POST /pedidos/callback-pagamentos`.
3. Quando o callback confirma o pagamento, `pedidos` publica os detalhes no tópico **`icompras.pedidos-pagos`**.
4. `faturamento` consome esse evento, gera a nota fiscal PDF com JasperReports, armazena o arquivo no bucket e publica os dados da nota no tópico **`icompras.pedidos-faturados`**.
5. `logistica` consome o evento de faturamento, prepara o envio e gera um código de rastreio. Em seguida publica **`icompras.pedidos-enviados`**.
6. `pedidos` consome as atualizações de faturamento e envio e persiste a mudança do estado do pedido.

Os eventos são mensagens JSON. Essa comunicação permite que faturamento e logística processem suas etapas sem bloquear a requisição inicial do pedido. A integração HTTP é usada para consultas imediatas de produto/cliente e para o callback de pagamento; Kafka encadeia o processamento posterior.

## Infraestrutura local

Requisitos: JDK 21, Maven, Docker e Docker Compose. Execute os comandos a partir da raiz do repositório, cada um em um terminal separado se quiser manter todos os recursos ativos:

```bash
docker compose -f icompras-servicos/database/docker-compose.yml up -d
docker compose -f icompras-servicos/broker/docker-compose.yml up -d
docker compose -f icompras-servicos/bucket/docker-compose.yml up -d
```

Portas configuradas nos arquivos de aplicação e compose:

| Recurso | Endereço local |
| --- | --- |
| PostgreSQL | `localhost:5555` |
| Kafka | `localhost:29092` |
| Kafka UI | `http://localhost:8090` |
| RustFS (API compatível com S3) | `http://localhost:9000` |
| Console RustFS | `http://localhost:9001` |
| `pedidos` | `http://localhost:8080` |
| `produtos` | `http://localhost:8081` |
| `clientes` | `http://localhost:8082` |
| `faturamento` | `http://localhost:8083` |
| `logistica` | `http://localhost:8084` |

As credenciais locais definidas nos arquivos de configuração são `postgres`/`postgres` para PostgreSQL e `minioadmin`/`minioadmin` para o serviço de bucket. São valores de desenvolvimento; não os use em ambientes compartilhados ou de produção.

## Banco de dados

O compose do PostgreSQL publica a porta `5555` e configura as bases `icomprasprodutos`, `icomprasclientes` e `icompraspedidos`. As configurações dos serviços apontam para bases separadas, e `ddl-auto` está definido como `none`, portanto o schema precisa existir antes de iniciar as aplicações. Consulte `icompras-servicos/database/postgres-schema.sql` para a estrutura inicial e `migration.sql` para a migração de inclusão da coluna `ativo` em clientes e produtos.

> Observação: o compose monta `database/data` como diretório de dados do PostgreSQL, mas não monta os arquivos SQL em `/docker-entrypoint-initdb.d`. A execução dos scripts de schema/migração é uma etapa manual. Além disso, o compose oficial da imagem `postgres` não processa por si só a variável `POSTGRES_MULTIPLE_DATABASES`; crie as bases conforme necessário antes de aplicar o schema.

## Executar os microserviços

Após iniciar os recursos externos, cada aplicação pode ser iniciada na pasta correspondente:

```bash
cd produtos
mvn spring-boot:run
```

Repita para `clientes`, `pedidos`, `faturamento/faturamento` e `logistica`. Inicie PostgreSQL, Kafka e o bucket antes dos serviços que dependem deles. As URLs de integração e as portas estão nos arquivos `src/main/resources/application.yml` de cada módulo; ajuste-as caso sua infraestrutura esteja em outro endereço.

## Observações

- A chamada de pagamento é feita por um cliente HTTP externo em `pedidos`; o callback representa a resposta desse fluxo.
- O SDK usado pelo faturamento é o MinIO, enquanto o compose local fornece RustFS compatível com S3. Verifique endpoint e credenciais no `application.yml` se substituir o armazenamento.
- O conteúdo e os nomes dos recursos descritos refletem a configuração existente neste repositório.
