# ViaSeguraAPI 📍

Bem-vindo ao **ViaSeguraAPI**!

## 📝 Índice

- [Visão Geral](#visão-geral)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Criação das Tabelas](#criação-das-tabelas)
- [Testando as Rotas](#testando-as-Rotas)
- [Observações Finais](#observações-finais)

---

## 🌟 Visão Geral

O ViaSeguraAPI tem como objetivo fornecer uma autenticação segura e o acesso aos dados de sinistro da cidade do Recife.

---

## 🛠 Pré-requisitos

- **Java JDK 21**
- **Maven 3.9.9**
- **Docker e Docker Compose (opcional)**
- **PostgreSQL 16.3**
- **pgAdmin 4 para gerenciamento da base de dados (Opcional)**

---

## ⚙️ Instalação

Siga os passos abaixo para configurar o ambiente de desenvolvimento:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/sugayamidori/ViaSegura.git
    ```
2.  **Navegue até o diretório do projeto:**
    ```bash
    cd ViaSegura/Backend/viaseguraapi
    ```
3.  **Instale as dependências:**
    ```bash
    mvn clean -U install -DskipTests
    ```

---

## 🚀 Configuração

### 🔧 Passos com Docker

O projeto como um todo apresenta um **[`docker-compose`](../docker-compose.yml) para rodar os serviços pelo Docker**.
Configure apenas como preferir as seguintes envs:

### viaseguraapi
```env
DATASOURCE_URL=jdbc:postgresql://localhost:5432/acervo
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=default
TZ=America/Sao_Paulo
```

Caso queira integrar com o Google:
```env
GOOGLE_CLIENT_ID=client_id
GOOGLE_CLIENT_SECRET=client_secret
```

### viaseguradb
```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=viasegura
TZ=America/Sao_Paulo
```

### pgadmin4
```env
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin
```

Após definir as variáveis de ambiente rode o seguinte comando:
```bash
    docker compose up --build -d
```

Este projeto utiliza **PostgreSQL**. Recomenda-se utilizar o Docker para subir o ambiente rapidamente.

📝 *Você pode alterar os valores das variáveis de ambiente conforme preferir.*

---

## 🧾 Criação das Tabelas

As instruções SQL estão no arquivo [`Scripts_SQL`](../Scripts_SQL). Verifique esse arquivo caso queira criar manualmente as tabelas.


---

## 📬 Testando as Rotas

Importe o arquivo `operacoes_postman.json` no [Postman](https://www.postman.com/) para testar as operações da API.

---

## 📂 Estrutura

```
📂 src/main/java/com/github/sugayamidori/viaseguraapi
 ├── 📂 config                 # Configurações gerais do projeto
 ├── 📂 controller             # Camada de endpoints da API
 ├── 📂 exceptions             # Camada de exceções personalizadas para tratamento de erros
 ├── 📂 model                  # Camada de mapeamento das entidades que compõem a base da dados
 ├── 📂 repository             # Camada de repositórios das entidades
 ├── 📂 security               # Camada de segurança do projeto
 ├── 📂 service                # Camada de serviço da API
 ├── 📂 validator              # Camada de validação das entidades
  Application                  # Inicializador da API
```

---

## 📌 Observações Finais

- Certifique-se de que as portas `5432` (PostgreSQL), `15432` (pgAdmin) e `8080` (ViaSeguraAPI) estejam livres no seu sistema.
- O projeto está estruturado para fácil deploy em containers e integração com serviços externos.