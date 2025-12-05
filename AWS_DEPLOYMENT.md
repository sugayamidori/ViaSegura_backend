# 🚀 Documentação CI/CD - Backend API ViaSegura

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Configuração no GitHub](#configuração-no-github)
4. [Configuração na AWS](#configuração-na-aws)
5. [Configuração no Docker Hub](#configuração-no-docker-hub)
6. [Configuração nos Servidores EC2](#configuração-nos-servidores-ec2)
7. [Estrutura de Arquivos](#estrutura-de-arquivos)
8. [Fluxo de Deploy](#fluxo-de-deploy)
9. [Troubleshooting](#troubleshooting)
10. [Melhores Práticas](#melhores-práticas)

---

## 🎯 Visão Geral

Este CI/CD automatiza:
- ✅ Build e testes do código Java
- ✅ Análise de cobertura de código
- ✅ Build de imagem Docker
- ✅ Push para Amazon ECR e Docker Hub
- ✅ Deploy automático em EC2 (Staging/Production)
- ✅ Health checks e rollback automático
- ✅ Scan de vulnerabilidades

### Ambientes

- **Staging**: Branch `stage` → Deploy automático
- **Production**: Branch `main` → Deploy automático (pode adicionar aprovação manual)

---

## ⚙️ Pré-requisitos

### Ferramentas Necessárias
- Conta AWS com EC2 e ECR
- Conta no Docker Hub
- Repositório no GitHub
- Java 21 e Maven configurados no projeto

### Conhecimentos
- Básico de Docker e Docker Compose
- SSH e chaves públicas/privadas
- AWS IAM (usuários e permissões)

---

## 🔧 Configuração no GitHub

### 1. Criar Secrets do Repositório

Vá em: `Settings` → `Secrets and variables` → `Actions` → `New repository secret`

#### Secrets AWS
```
AWS_ACCESS_KEY_ID
Valor: AKIA******************

AWS_SECRET_ACCESS_KEY
Valor: sua-secret-key-aqui

AWS_REGION
Valor: us-east-1

AWS_ECR_REGISTRY
Valor: 123456789012.dkr.ecr.us-east-1.amazonaws.com
```

#### Secrets Docker Hub
```
DOCKER_USERNAME
Valor: seu-usuario-dockerhub

DOCKER_ACCESS_TOKEN
Valor: dckr_pat_*********************
```

#### Secrets EC2 - Production
```
EC2_PROD_HOST
Valor: 54.123.45.67

EC2_PROD_USER
Valor: ubuntu

EC2_PROD_SSH_KEY
Valor: -----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn
...
(cole sua chave privada completa aqui)
...
-----END OPENSSH PRIVATE KEY-----
```

#### Secrets EC2 - Staging
```
EC2_STAGING_HOST
Valor: 52.98.76.54

EC2_STAGING_USER
Valor: ubuntu

EC2_STAGING_SSH_KEY
Valor: -----BEGIN OPENSSH PRIVATE KEY-----
(sua chave privada de staging)
-----END OPENSSH PRIVATE KEY-----
```

### 2. Criar Environments (Opcional mas Recomendado)

Vá em: `Settings` → `Environments`

#### Criar Environment "production"
- Nome: `production`
- ✅ Marcar "Required reviewers" (opcional)
- Adicionar revisores: equipe de DevOps/Tech Lead
- Adicionar branch protection: apenas `main` pode fazer deploy

#### Criar Environment "staging"
- Nome: `staging`
- Sem restrições (deploy automático)

---

## ☁️ Configuração na AWS

### 1. Criar Usuário IAM para CI/CD

```bash
# No AWS Console:
IAM → Users → Add user
- Nome: github-actions-viasegura
- Access type: Programmatic access
```

**Policies necessárias:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:PutImage",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload",
        "ecr:BatchGetImage",
        "ecr:DescribeImages"
      ],
      "Resource": "*"
    }
  ]
}
```

### 2. Criar Repositório no Amazon ECR

```bash
# Via AWS CLI
aws ecr create-repository \
  --repository-name viaseguraapi \
  --region us-east-1 \
  --image-scanning-configuration scanOnPush=true

# Via Console:
ECR → Repositories → Create repository
- Nome: viaseguraapi
- Scan on push: Enabled
```

### 3. Configurar Security Groups da EC2

```bash
# Regras de entrada necessárias:
- SSH (22): Seu IP ou GitHub Actions IPs
- HTTP (80): 0.0.0.0/0
- HTTPS (443): 0.0.0.0/0
- Custom TCP (8080): 0.0.0.0/0 (Staging)
- Custom TCP (8081): 0.0.0.0/0 (Production)
```

### 4. Criar Instâncias EC2

#### EC2 Staging
```bash
- AMI: Ubuntu 22.04 LTS
- Type: t3.medium (mínimo)
- Storage: 30GB GP3
- Tags:
  - Name: viasegura-backend-staging
  - Environment: staging
```

#### EC2 Production
```bash
- AMI: Ubuntu 22.04 LTS
- Type: t3.large (recomendado)
- Storage: 50GB GP3
- Tags:
  - Name: viasegura-backend-production
  - Environment: production
```

---

## 🐳 Configuração no Docker Hub

### 1. Criar Access Token

```bash
# Docker Hub → Account Settings → Security → New Access Token
- Description: github-actions-viasegura
- Access permissions: Read, Write, Delete
```

### 2. Criar Repositório

```bash
# Docker Hub → Repositories → Create Repository
- Name: viaseguraapi
- Visibility: Private (recomendado)
```

---

## 🖥️ Configuração nos Servidores EC2

### 1. Preparar o Servidor (Execute em AMBOS staging e production)

```bash
# SSH no servidor
ssh ubuntu@SEU_IP_EC2

# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Instalar AWS CLI
sudo apt install awscli -y
aws configure
# AWS Access Key ID: (use as mesmas do GitHub)
# AWS Secret Access Key: (use as mesmas do GitHub)
# Default region: us-east-1
# Default output format: json

# Relogar para aplicar permissões do Docker
exit
ssh ubuntu@SEU_IP_EC2
```

### 2. Criar Estrutura de Diretórios

```bash
# Criar diretório da aplicação
mkdir -p /home/ubuntu/viasegura
cd /home/ubuntu/viasegura
```

### 3. Criar docker-compose.yml

```bash
nano docker-compose.yml
```

Cole o conteúdo:
```yaml
version: '3.8'

services:
  app-backend:
    image: ${BACKEND_IMAGE}
    container_name: viasegura-backend-${ENVIRONMENT:-production}
    restart: unless-stopped
    ports:
      - "${BACKEND_PORT:-8080}:8080"
    environment:
      # Spring Profile
      SPRING_PROFILES_ACTIVE: ${ENVIRONMENT:-production}
      
      # Database
      SPRING_DATASOURCE_URL: ${DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DATASOURCE_PASSWORD}
      
      # JWT
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION:-86400000}
      
      # Server
      SERVER_PORT: 8080
      
    networks:
      - viasegura-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

networks:
  viasegura-network:
    driver: bridge
```

### 4. Criar Arquivo .env para Staging

```bash
# No servidor STAGING
nano .env.staging
```

Cole e ajuste:
```env
ENVIRONMENT=staging
BACKEND_PORT=8081
BACKEND_IMAGE=123456789012.dkr.ecr.us-east-1.amazonaws.com/viaseguraapi:stage

# Database Staging
DATASOURCE_URL=jdbc:postgresql://db-staging.viasegura.com:5432/viasegura_staging
DATASOURCE_USERNAME=viasegura_user_stage
DATASOURCE_PASSWORD=SuaSenhaSeguraStaging123!

# JWT Staging
JWT_SECRET=staging-jwt-secret-key-change-this-in-production-12345678
JWT_EXPIRATION=86400000
```

### 5. Criar Arquivo .env para Production

```bash
# No servidor PRODUCTION
nano .env.production
```

Cole e ajuste:
```env
ENVIRONMENT=production
BACKEND_PORT=8080
BACKEND_IMAGE=123456789012.dkr.ecr.us-east-1.amazonaws.com/viaseguraapi:latest

# Database Production
DATASOURCE_URL=jdbc:postgresql://db-prod.viasegura.com:5432/viasegura_production
DATASOURCE_USERNAME=viasegura_user_prod
DATASOURCE_PASSWORD=SuaSenhaSeguraProdXYZ789!@#

# JWT Production
JWT_SECRET=production-jwt-super-secret-key-very-long-and-secure-987654321
JWT_EXPIRATION=86400000
```

### 6. Testar Configuração

```bash
# Teste se o docker-compose está ok
docker-compose --env-file .env.staging config
# ou
docker-compose --env-file .env.production config

# Verificar se AWS CLI está configurado
aws ecr describe-repositories --region us-east-1

# Teste de login no ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com
```

---

## 📁 Estrutura de Arquivos

### No Repositório GitHub

```
viasegura-backend/
├── .github/
│   └── workflows/
│       └── deploy-backend.yml         # Workflow CI/CD
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-staging.yml
│   │       └── application-production.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml                 # Para referência
├── pom.xml
└── README.md
```

### Nos Servidores EC2

```
/home/ubuntu/viasegura/
├── docker-compose.yml
├── .env.staging          # Apenas no servidor staging
├── .env.production       # Apenas no servidor production
└── logs/                 # Opcional: para logs persistentes
```

---

## 🔄 Fluxo de Deploy

### Deploy para Staging

```bash
# 1. Criar branch e fazer alterações
git checkout -b feature/nova-funcionalidade

# 2. Commit e push
git add .
git commit -m "feat: adiciona nova funcionalidade"
git push origin feature/nova-funcionalidade

# 3. Criar Pull Request para 'stage'
# GitHub UI → Create Pull Request → Base: stage

# 4. Merge na branch stage
# Após aprovação → Merge

# 5. Deploy automático inicia!
# GitHub Actions → Veja os logs em tempo real
```

**O que acontece:**
1. ✅ Build e testes executam
2. ✅ Imagem Docker é criada
3. ✅ Push para ECR e Docker Hub
4. ✅ Deploy automático no servidor staging
5. ✅ Health check valida a aplicação

### Deploy para Production

```bash
# 1. Validar em staging primeiro
# Testar: https://api-staging.viasegura.com

# 2. Criar Pull Request para 'main'
# GitHub UI → Create Pull Request → Base: main

# 3. Revisão e aprovação
# Aguardar aprovação dos reviewers (se configurado)

# 4. Merge na branch main
# Após aprovação → Merge

# 5. Deploy automático em production!
# Pode ter aprovação manual se configurado environment
```

---

## 🔧 Troubleshooting

### Problema: Build falha no Maven

```bash
# Verificar logs no GitHub Actions
# Comum: dependências não encontradas

# Solução:
- Verificar pom.xml
- Limpar cache: delete cache do Maven nas Actions
- Verificar versão do Java
```

### Problema: Falha no login ECR

```bash
# Erro: "denied: Your authorization token has expired"

# Solução no servidor EC2:
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin SEU_ECR_REGISTRY

# Verificar credenciais AWS:
aws sts get-caller-identity
```

### Problema: Container não fica healthy

```bash
# Conectar no servidor
ssh ubuntu@SEU_IP_EC2

# Ver logs do container
docker logs viasegura-backend-staging --tail 100

# Ver status
docker ps -a

# Testar health check manualmente
docker exec viasegura-backend-staging curl localhost:8080/actuator/health

# Verificar variáveis de ambiente
docker exec viasegura-backend-staging env | grep SPRING
```

### Problema: Aplicação não conecta no banco

```bash
# Verificar variáveis de ambiente
docker exec viasegura-backend-staging env | grep DATASOURCE

# Testar conexão com banco
docker exec viasegura-backend-staging nc -zv db-host 5432

# Verificar logs específicos
docker logs viasegura-backend-staging 2>&1 | grep -i "database\|connection"
```

### Problema: Deploy falha no SSH

```bash
# Erro: "Permission denied (publickey)"

# Verificar:
1. Secret EC2_PROD_SSH_KEY está correto
2. Chave não tem passphrase
3. Servidor permite conexão do IP do GitHub Actions

# Testar conexão manual:
ssh -i sua-chave.pem ubuntu@SEU_IP_EC2
```

---

## ✅ Melhores Práticas

### Segurança

1. **Nunca commitar secrets no código**
   - Use sempre GitHub Secrets
   - Use .env files no servidor
   - Adicione .env* no .gitignore

2. **Rotacionar credenciais regularmente**
   - AWS Access Keys: a cada 90 dias
   - JWT Secrets: a cada release major
   - Senhas de banco: conforme política

3. **Usar HTTPS sempre**
   - Configure SSL/TLS no nginx
   - Use AWS Certificate Manager
   - Redirecione HTTP → HTTPS

### Performance

1. **Usar cache eficientemente**
   - Cache do Maven no GitHub Actions
   - Docker layer caching
   - Cache de dependências

2. **Otimizar imagem Docker**
   ```dockerfile
   # Multi-stage build
   FROM maven:3.9-eclipse-temurin-21 AS build
   WORKDIR /app
   COPY pom.xml .
   RUN mvn dependency:go-offline
   COPY src ./src
   RUN mvn clean package -DskipTests

   FROM eclipse-temurin:21-jre-alpine
   WORKDIR /app
   COPY --from=build /app/target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

3. **Limpar recursos antigos**
   - Imagens Docker: `docker image prune`
   - Logs: Configurar rotação
   - Backups: Manter apenas últimos 3

### Monitoramento

1. **Logs estruturados**
   - Use JSON para logs
   - Envie para CloudWatch ou similar
   - Configure alertas

2. **Métricas importantes**
   - Tempo de build
   - Taxa de sucesso dos deploys
   - Tempo de resposta da aplicação
   - Uso de recursos (CPU/RAM)

3. **Health checks robustos**
   - Verificar conexão com banco
   - Verificar serviços externos
   - Endpoint: `/actuator/health`

### Rollback

1. **Manter backups automáticos**
   - Workflow faz backup antes do deploy
   - Manter últimas 3 versões

2. **Rollback manual rápido**
   ```bash
   # SSH no servidor
   ssh ubuntu@SEU_IP_EC2
   
   # Listar backups
   docker images | grep backup
   
   # Fazer rollback
   docker stop viasegura-backend-production
   docker rm viasegura-backend-production
   docker tag viasegura-backend-production-backup:20240101-120000 viasegura-backend-production:latest
   docker-compose up -d app-backend
   ```

### Testes

1. **Sempre testar em staging primeiro**
2. **Manter cobertura de testes > 80%**
3. **Incluir testes de integração**
4. **Smoke tests após deploy**

---

## 📞 Suporte

### Contatos
- **Tech Lead**: tech@viasegura.com

### Documentação Adicional
- [AWS ECR Docs](https://docs.aws.amazon.com/ecr/)
- [Docker Compose](https://docs.docker.com/compose/)
- [GitHub Actions](https://docs.github.com/actions)

### Logs e Monitoramento
- GitHub Actions: https://github.com/seu-org/viasegura-backend/actions
- AWS CloudWatch: https://console.aws.amazon.com/cloudwatch
- Servidor Staging: ssh ubuntu@STAGING_IP → `docker logs -f viasegura-backend-staging`
- Servidor Production: ssh ubuntu@PROD_IP → `docker logs -f viasegura-backend-production`

---

## 🎉 Checklist de Implementação

Use este checklist para garantir que tudo está configurado:

### AWS
- [ ] Usuário IAM criado com permissões ECR
- [ ] Repositório ECR criado (viaseguraapi)
- [ ] EC2 Staging criada e configurada
- [ ] EC2 Production criada e configurada
- [ ] Security Groups configurados
- [ ] AWS CLI configurado nas EC2s

### GitHub
- [ ] Secrets AWS configurados (3 secrets)
- [ ] Secrets Docker Hub configurados (2 secrets)
- [ ] Secrets EC2 Staging configurados (3 secrets)
- [ ] Secrets EC2 Production configurados (3 secrets)
- [ ] Environments criados (opcional)
- [ ] Workflow file commitado

### Docker Hub
- [ ] Access Token criado
- [ ] Repositório criado (viaseguraapi)

### Servidores EC2
- [ ] Docker instalado (staging + production)
- [ ] Docker Compose instalado (staging + production)
- [ ] AWS CLI configurado (staging + production)
- [ ] Diretório /home/ubuntu/viasegura criado
- [ ] docker-compose.yml criado
- [ ] .env.staging criado (apenas staging)
- [ ] .env.production criado (apenas production)
- [ ] Teste de login ECR funcionando

### Aplicação
- [ ] Dockerfile criado e testado
- [ ] application-staging.yml configurado
- [ ] application-production.yml configurado
- [ ] Endpoint /actuator/health funcionando
- [ ] Testes unitários passando
- [ ] Testes de integração passando

### Validação Final
- [ ] Push na branch stage → Deploy staging OK
- [ ] Aplicação staging acessível
- [ ] Health check staging OK
- [ ] Push na branch main → Deploy production OK
- [ ] Aplicação production acessível
- [ ] Health check production OK

---

**🎊 Parabéns! Seu CI/CD está pronto para uso!**