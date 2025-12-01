#!/bin/bash
set -e

echo "========================================="
echo "CARREGADOR DE DADOS - H3 ACIDENTES"
echo "Auto-inicialização do container"
echo "========================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Instala dependências PRIMEIRO
echo -e "${BLUE}Instalando dependências Python...${NC}"
pip install -q --no-cache-dir -r requirements.txt
echo -e "${GREEN}✓ Dependências instaladas${NC}"

# Aguarda banco estar pronto
echo -e "${BLUE}Aguardando banco de dados...${NC}"
MAX_RETRIES=30
RETRY=0

until python -c "import psycopg2; psycopg2.connect(host='viaseguradb', database='viasegura', user='postgres', password='postgres', port=5432)" 2>/dev/null; do
    RETRY=$((RETRY+1))
    if [ $RETRY -eq $MAX_RETRIES ]; then
        echo -e "${RED}✗ Timeout aguardando banco de dados${NC}"
        echo -e "${YELLOW}Verificando logs do banco...${NC}"
        docker logs viaseguradb --tail 20
        exit 1
    fi
    echo -e "${YELLOW}Tentativa $RETRY/$MAX_RETRIES...${NC}"
    sleep 2
done
echo -e "${GREEN}✓ Banco de dados conectado${NC}"

# Executa carga de dados
echo -e "\n${GREEN}═══════════════════════════════════════${NC}"
echo -e "${GREEN}Iniciando carga de dados...${NC}"
echo -e "${GREEN}═══════════════════════════════════════${NC}\n"

python load_data.py

EXIT_CODE=$?

echo -e "\n${GREEN}═══════════════════════════════════════${NC}"
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ Carga concluída com sucesso!${NC}"
else
    echo -e "${RED}✗ Erro na carga de dados (código: $EXIT_CODE)${NC}"
    exit $EXIT_CODE
fi
echo -e "${GREEN}═══════════════════════════════════════${NC}\n"

# Mantém container rodando
echo -e "${BLUE}Container pronto. Mantendo ativo...${NC}"
tail -f /dev/null