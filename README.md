# ViaSeguraAPI 📍

Bem-vindo ao **ViaSeguraAPI**!

## 📝 Índice

- [Visão Geral](#visão-geral)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Criação das Tabelas](#criação-das-tabelas)
- [Testando as Rotas](#testando-as-Rotas)
- [Deployment AWS](#deployment-aws)
- [Observações Finais](#observações-finais)

---

## 🌟 Visão Geral

O ViaSeguraAPI visa fornecer uma autenticação segura e o acesso aos dados de sinistro da cidade do Recife.

---

## 🛠 Pré-requisitos

- **Java JDK 21**
- **Maven 3.9.9**
- **Docker e Docker Compose (opcional)**
- **PostgreSQL 16.3**
- **pgAdmin 4 para gestão da base de dados (Opcional)**

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

O projeto apresenta um **[`docker-compose`](docker-compose.yml) para rodar os serviços pelo Docker**.
Configure apenas como preferir as seguintes envs:

### viaseguraapi
```env
DATASOURCE_URL=jdbc:postgresql://viaseguradb:5432/viasegura
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=postgres
JWT_SECRET=my-secret
JWT_EXPIRE=3600000
ORIGIN_PATTERNS=http://localhost:3000,http://localhost:8080,http://localhost:5173
SPRING_PROFILES_ACTIVE=dev
TZ=America/Recife
```

### viaseguradb
```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=viasegura
TZ=America/Recife
```

### pgadmin4
```env
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin
```

### data-loader
Para esse serviço você deve usar os arquivos .csv que estão em *[`data`](scripts/data)* para serem salvos numa
base de dados. Caso esteja num sistema operacional windows, coloque o separador de linhas como LF para o
[`entrypoint.sh`](scripts/entrypoint.sh) executar a inserção de dados automaticamente ao subir o container.
Caso queira executar manualmente, execute esse comando:
```bash
# Instala dependências
docker exec data-loader pip install -r requirements.txt

# Executa carga
docker exec -it data-loader python load_data.py
```


Após definir as variáveis de ambiente rode o seguinte comando:
```bash
    docker compose up --build -d
```

Este projeto utiliza **PostgreSQL**. Recomenda-se utilizar o Docker para subir o ambiente rapidamente.

📝 *Você pode alterar os valores das variáveis de ambiente conforme preferir.*

---

## 🧾 Criação das Tabelas

A criação de tabelas é realizada automaticamente após rodar a aplicação utilizando as 
migrations com flyway em [migration](src/main/resources/db/migration)


---

## 📬 Testando as Rotas

Importe o arquivo [Via Segura API.postman_collection](postman/collections/Via%20Segura%20API.postman_collection.json) 
disponível no diretório root deste repositório
ou acesse a documentação com Swagger acessando `/swagger-ui/index.html` e importe a url `/v3/api-docs`
no [Postman](https://www.postman.com/) para testar as operações da API.

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

## 🚀 Deployment AWS

Para realizar o deployment da aplicação com AWS, siga os passos em [AWS_DEPLOYMENT](AWS_DEPLOYMENT.md)

---

## 📌 Observações Finais

- Certifique-se de que as portas `5432` (PostgreSQL), `15432` (pgAdmin) e `8080` (ViaSeguraAPI) estejam livres no seu sistema.
- O projeto está estruturado para fácil deploy em containers e integração com serviços externos.