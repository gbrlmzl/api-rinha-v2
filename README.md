<div align="center">

# ⚔️ Rinha do Campus IV — API

> Backend da plataforma de gerenciamento do torneio de League of Legends **Rinha do Campus IV**, realizado por estudantes do Campus IV da UFPB.

<br/>

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-4169E1?style=for-the-badge&logo=postgresql)
![Flyway](https://img.shields.io/badge/Flyway-12.2.0-CC0200?style=for-the-badge&logo=flyway)
![MercadoPago](https://img.shields.io/badge/Mercado%20Pago-SDK%202.8.0-009EE3?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-alpine-2496ED?style=for-the-badge&logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger)
![License](https://img.shields.io/badge/Licença-MIT-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-v2%20estável%20%7C%20v3%20planejada-blue?style=for-the-badge)

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Status do Projeto](#-status-do-projeto)
- [Funcionalidades e Módulos](#-funcionalidades-e-módulos)
- [Integrações](#-integrações)
- [Acesso ao Projeto](#-acesso-ao-projeto)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Como Rodar Localmente](#-como-rodar-localmente)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Documentação da API](#-documentação-da-api)
- [Pessoas Contribuidoras](#-pessoas-contribuidoras)
- [Desenvolvedores](#-desenvolvedores)
- [Licença](#-licença)

---

## 📖 Sobre o Projeto

A **api-rinha-v2** é o backend da plataforma **Rinha do Campus IV**, um torneio de League of Legends organizado por estudantes do Campus IV da UFPB. A API provê todos os serviços que o frontend [`site-rinha-v2`](https://github.com/gbrlmzl/site-rinha-v2) consome: autenticação, gerenciamento de torneios, processamento de pagamentos, envio de e-mails e comunicação em tempo real via WebSocket.

Construída com **Spring Boot 4** e **Java 25**, seguindo as boas práticas de uma API REST com autenticação stateless via JWT.

---

## 🚀 Status do Projeto

O projeto encontra-se atualmente na **v2**, em estado estável. A **v3** está em fase de planejamento, com novas funcionalidades sendo mapeadas.

---

## ✨ Funcionalidades e Módulos

### 🔐 Módulo de Autenticação
- Autenticação stateless com **JWT** (Auth0 java-jwt ^4.5.1)
- Segurança gerenciada pelo **Spring Security**
- Cookies com flag `Secure` configurável por ambiente (dev/prod)
- Controle de acesso por perfis (admin / participante)

### 🏆 Módulo de Torneios
- Criação, listagem, atualização e cancelamento de torneios
- Gerenciamento de inscrições de times e jogadores
- Controle de status e vagas disponíveis

### 💳 Módulo de Pagamento
- Integração com a **API do Mercado Pago** (SDK Java 2.8.0)
- Geração de cobranças PIX com **QR Code** (ZXing 3.5.3)
- Webhook para recebimento de notificações de pagamento
- Atualização de status em tempo real via **WebSocket**

### 📡 Módulo de WebSocket
- Comunicação em tempo real com o frontend via **STOMP** sobre WebSocket
- Notificações instantâneas de confirmação de pagamento
- Implementado com `spring-boot-starter-websocket`

### 📧 Módulo de E-mail
- Envio de e-mails transacionais via **Gmail SMTP** (`spring-boot-starter-mail`)
- Templates HTML renderizados com **Thymeleaf**
- E-mails de confirmação de inscrição e pagamento

### 🖼️ Módulo de Imagens
- Upload de imagens via integração com a **API do Imgur**

### 🛠️ Módulo Administrativo
- Endpoints protegidos para gerenciamento completo da plataforma pelo admin

---

## 🔌 Integrações

| Serviço | Finalidade |
|---|---|
| **Mercado Pago** | Processamento de pagamentos PIX e cartão |
| **Imgur** | Upload e hospedagem de imagens |
| **Gmail SMTP** | Envio de e-mails transacionais |
| **ZXing (Google)** | Geração de QR Codes para pagamento |

---

## 🔗 Acesso ao Projeto

> ⚠️ Informe aqui o link da API em produção quando disponível.

```
https://api.seudominio.com.br
```

Documentação Swagger (quando rodando): `http://localhost:8080/swagger-ui.html`

Repositório: [github.com/gbrlmzl/api-rinha-v2](https://github.com/gbrlmzl/api-rinha-v2)

---

## 🛠️ Tecnologias Utilizadas

### Core

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Java](https://openjdk.org/) | 25 | Linguagem principal |
| [Spring Boot](https://spring.io/projects/spring-boot) | 4.0.3 | Framework principal |
| [Maven](https://maven.apache.org/) | — | Gerenciamento de dependências e build |

### Persistência

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | — | Abstração de acesso a dados |
| [PostgreSQL](https://www.postgresql.org/) | latest | Banco de dados relacional |
| [Flyway](https://flywaydb.org/) | 12.2.0 | Migrações de banco de dados |

### Segurança

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Spring Security](https://spring.io/projects/spring-security) | — | Autenticação e autorização |
| [Auth0 Java JWT](https://github.com/auth0/java-jwt) | 4.5.1 | Geração e validação de tokens JWT |

### Integrações & Serviços

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Mercado Pago SDK Java](https://github.com/mercadopago/sdk-java) | 2.8.0 | Processamento de pagamentos |
| [Google ZXing Core](https://github.com/zxing/zxing) | 3.5.3 | Geração de QR Code |
| [Google ZXing JavaSE](https://github.com/zxing/zxing) | 3.5.3 | Suporte desktop para ZXing |
| [Spring Mail](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail) | — | Envio de e-mails via SMTP |
| [Thymeleaf](https://www.thymeleaf.org/) | — | Templates HTML para e-mails |
| [Spring WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket) | — | Comunicação em tempo real |

### Documentação

| Tecnologia | Versão | Descrição |
|---|---|---|
| [SpringDoc OpenAPI](https://springdoc.org/) | 3.0.3 | Geração automática de Swagger UI |
| [SLF4J](https://www.slf4j.org/) | 2.0.17 | API de logging |

### Produtividade

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Lombok](https://projectlombok.org/) | — | Redução de boilerplate (getters, builders etc.) |
| [Spring DevTools](https://docs.spring.io/spring-boot/reference/using/devtools.html) | — | Hot reload em desenvolvimento |

### DevOps

| Tecnologia | Versão | Descrição |
|---|---|---|
| [Docker](https://www.docker.com/) | eclipse-temurin (alpine) | Containerização da aplicação |
| [GitHub Actions](https://github.com/features/actions) | — | CI/CD automatizado |

---

## ⚙️ Como Rodar Localmente

### Pré-requisitos
- Java 25
- Maven
- PostgreSQL rodando localmente (ou via Docker)

### Passos

```bash
# Clone o repositório
git clone https://github.com/gbrlmzl/api-rinha-v2.git
cd api-rinha-v2

# Copie e configure as variáveis de ambiente
cp .env.example .env
# edite o .env com suas credenciais

# Rode com Maven
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Com Docker

```bash
docker build -t api-rinha-v2 .
docker run --env-file .env -p 8080:8080 api-rinha-v2
```

---

## 🔐 Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com base no `.env.example`:

```env
# Perfil Spring (dev | prod)
SPRING_PROFILES_ACTIVE=dev

# Banco de dados
POSTGRES_RINHA_URL=jdbc:postgresql://localhost:5432/rinha
POSTGRES_USERNAME=rinha
POSTGRES_PASSWORD=troque-isso
POSTGRES_DB=rinha

# Segurança
JWT_SECRET=troque-isso-em-prod
APP_COOKIES_SECURE=false   # true em produção (HTTPS)

# Mercado Pago
MERCADOPAGO_ACCESS_TOKEN=
MERCADOPAGO_TEST_TOKEN=

# Imgur
IMGUR_ACCESS_TOKEN=

# E-mail (Gmail SMTP)
GMAIL_USERNAME=
GMAIL_APP_PASSWORD=

# Frontend
FRONTEND_URL=http://localhost:3000
```

---

## 📚 Documentação da API

Com a aplicação rodando, acesse a documentação interativa gerada pelo **SpringDoc OpenAPI**:

```
http://localhost:8080/swagger-ui.html
```

---

## 🤝 Pessoas Contribuidoras

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ryanpsouzaa">
        <img src="https://github.com/ryanpsouzaa.png" width="80px;" alt="ryanpsouzaa"/><br/>
        <sub><b>ryanpsouzaa</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 👨‍💻 Desenvolvedores

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/gbrlmzl">
        <img src="https://github.com/gbrlmzl.png" width="80px;" alt="gbrlmzl"/><br/>
        <sub><b>gbrlmzl</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/victorhugosalv">
        <img src="https://github.com/victorhugosalv.png" width="80px;" alt="victorhugosalv"/><br/>
        <sub><b>victorhugosalv</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">
  Feito com ☕ e 🎮 por estudantes do Campus IV da UFPB
</div>
