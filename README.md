# EventMaster - Sistema de Gerenciamento de Eventos

## O que é o projeto

EventMaster é uma aplicação web completa para gerenciamento de eventos e inscrições. O sistema permite a criação, visualização e administração de eventos, além de gerenciar inscrições de participantes. A aplicação possui um sistema de autenticação com diferentes níveis de acesso:

- **Administrador**: Gerenciamento completo de usuários e todos os eventos
- **Organizador**: Criação e gerenciamento de seus próprios eventos
- **Participante**: Visualização e inscrição em eventos

## Principais funcionalidades

- Autenticação e autorização com diferentes níveis de acesso
- Criação e edição de eventos com diferentes tipos (palestras, cursos, oficinas, etc.)
- Sistema de inscrição em eventos com capacidade limitada
- Gerenciamento de registros (confirmação de presença, cancelamento)
- Painel administrativo para gestão de usuários
- Interface amigável e responsiva

## Tecnologias utilizadas

- Java 24
- Spring Boot 3.5.0
- Spring MVC
- Spring Security
- Spring Data JPA
- Spring Validation
- Thymeleaf (com extras para Spring Security)
- PostgreSQL (banco de dados)
- Docker/Docker Compose (containerização)
- Bootstrap 5 (framework CSS)
- Bootstrap Icons
- Maven (gerenciamento de dependências)
- Lombok (redução de código boilerplate)
- JUnit e Spring Test (para testes)

## Como instalar e rodar

### Pré-requisitos:

- Java 24 JDK
- Docker e Docker Compose
- Maven
- Git

### Passos para instalação:

1. Inicie o banco de dados PostgreSQL com Docker:

   ```
   docker compose up -d
   ```

2. Compile e execute a aplicação:

   ```
   ./mvnw spring-boot:run
   ```

3. Acesse a aplicação no navegador:
   ```
   http://localhost:8080
   ```

### Dados de acesso padrão:

- Admin: `admin@example.com` / `password`
- Organizador: `joao.organizador@example.com` / `password`
- Participante: `carlos@example.com` / `password`

### Variáveis de ambiente (opcionais):

Se quiser personalizar a conexão com o banco de dados, configure as seguintes variáveis:

- `DB_URL`: URL de conexão com o banco (padrão: jdbc:postgresql://localhost:5432/mydatabase)
- `DB_USERNAME`: Usuário do banco (padrão: myuser)
- `DB_PASSWORD`: Senha do banco (padrão: secret)

## Autores:

- [@Acauhi99](https://github.com/Acauhi99)
- [@Menon04](https://github.com/Menon04)
