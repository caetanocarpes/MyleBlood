[README.md](https://github.com/user-attachments/files/21584751/README.md)

# 🩸 Backend - Sistema de Doação de Sangue

Este é o backend oficial do sistema de doação de sangue, construído com **Spring Boot**, focado em facilitar o cadastro de usuários, agendamento de doações, visualização de postos e gerenciamento via painel administrativo.

---

## ⚙️ Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Security + JWT
- JPA / Hibernate
- Banco de dados (H2, PostgreSQL ou outro)
- Lombok
- Bean Validation

---

## 📁 Estrutura de pacotes

```
com.sangue.api
├── controller      # Endpoints REST
├── dto             # Data Transfer Objects
├── entity          # Entidades JPA
├── repository      # Interfaces JPA
├── security        # Autenticação JWT
├── service         # Regras de negócio
├── handler         # Tratamento global de exceções
└── SangueAppApplication.java
```

---

## 🧪 Endpoints principais

### 🔐 Autenticação (`/auth`)
| Método | Rota           | Descrição                      |
|--------|----------------|-------------------------------|
| POST   | `/register`    | Cria novo usuário              |
| POST   | `/login`       | Retorna token JWT              |
| GET    | `/me`          | Retorna dados do usuário logado|

---

### 👤 Usuários (`/api/usuarios`)
| Método | Rota                 | Descrição                  |
|--------|----------------------|-----------------------------|
| GET    | `/`                  | Lista todos os usuários     |
| GET    | `/{id}`              | Busca usuário por ID        |
| PUT    | `/{id}`              | Atualiza usuário            |
| DELETE | `/{id}`              | Deleta usuário              |

---

### 🏥 Postos (`/postos`)
| Método | Rota     | Descrição             |
|--------|----------|------------------------|
| GET    | `/`      | Lista todos os postos  |

---

### 📅 Agendamentos (`/agendamentos`)
| Método | Rota            | Descrição                                       |
|--------|------------------|--------------------------------------------------|
| POST   | `/`              | Cria um novo agendamento (com JWT)              |
| GET    | `/me`            | Lista agendamentos do usuário logado            |
| DELETE | `/{id}`          | Cancela agendamento (se for do próprio usuário) |
| GET    | `/ocupados`      | Lista horários ocupados de um posto e data      |

---

### 🛠️ Admin (`/api/admin`)
| Método | Rota                          | Descrição                                |
|--------|-------------------------------|-------------------------------------------|
| GET    | `/dashboard`                  | Retorna total de usuários, postos, etc.   |
| GET    | `/ranking-postos`            | Ranking de postos com mais doações       |
| GET    | `/historico-doacoes/{id}`    | Histórico de doações de um usuário       |

---

## ✅ Regras de negócio

- Somente usuários entre **16 e 69 anos** podem se cadastrar.
- Só é possível fazer um novo agendamento **após 60 dias** da última doação.
- As senhas são criptografadas com `BCrypt`.
- Todos os endpoints protegidos exigem **JWT no header Authorization**.
- CORS liberado (`*`) para facilitar testes com frontend local.

---

## 🔒 Autenticação JWT

Após o login, você receberá um token no formato:

```
Bearer eyJhbGciOiJIUzI1...
```

Use esse token nos headers das requisições autenticadas:

```
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 💻 Como rodar localmente

1. Clone o projeto:
   ```bash
   git clone https://github.com/seu-usuario/seu-repo.git
   cd backend
   ```

2. Compile e rode:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Acesse em: `http://localhost:8080`

---

## 🧠 Futuras melhorias (opcional)

- Upload de imagem de perfil
- Geração de relatório em PDF
- Painel de edição de perfil
- Refresh token para manter login ativo

---

## 📄 Licença

Projeto desenvolvido por [Caetano Carpes](https://github.com/caetanocarpes) — uso livre para fins educacionais e projetos open-source.
