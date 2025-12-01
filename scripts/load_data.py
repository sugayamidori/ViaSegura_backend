import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
from io import StringIO
import time
import os
import sys

class H3DataLoader:
    def __init__(self, db_config):
        """Inicializa conexão com banco"""
        print("Conectando ao banco de dados...")
        try:
            self.conn = psycopg2.connect(**db_config)
            self.cursor = self.conn.cursor()
            print("✓ Conectado com sucesso!")

            # Garante que a extensão UUID está habilitada
            self.cursor.execute('CREATE EXTENSION IF NOT EXISTS "uuid-ossp"')
            self.conn.commit()
        except Exception as e:
            print(f"✗ Erro ao conectar: {e}")
            sys.exit(1)

    def load_coordinates(self, csv_path, batch_size=5000):
        """Carrega coordenadas H3 (ignora duplicatas completas)"""
        print(f"\n{'='*60}")
        print(f"Carregando coordenadas de {csv_path}...")
        print(f"{'='*60}")

        if not os.path.exists(csv_path):
            print(f"✗ Arquivo não encontrado: {csv_path}")
            return

        df = pd.read_csv(csv_path)
        df = df.dropna(subset=['h3_cell', 'latitude', 'longitude'])

        total = len(df)
        print(f"Total de registros no CSV: {total:,}")

        data = [
            (row['h3_cell'], row['latitude'], row['longitude'], row['bairro_clean'])
            for _, row in df.iterrows()
        ]

        start_time = time.time()
        inserted = 0

        for i in range(0, total, batch_size):
            batch = data[i:i + batch_size]

            # Usando execute_values com ON CONFLICT DO NOTHING
            template = "(h3_cell, latitude, longitude, bairro_clean) VALUES %s ON CONFLICT (h3_cell, latitude, longitude, bairro_clean) DO NOTHING"

            execute_values(
                self.cursor,
                f"""
                WITH inserted AS (
                    INSERT INTO h3_coordinates {template}
                    RETURNING id
                )
                SELECT COUNT(*) FROM inserted
                """,
                batch,
                page_size=batch_size,
                fetch=True
            )

            result = self.cursor.fetchone()
            batch_inserted = result[0] if result else 0
            inserted += batch_inserted
            self.conn.commit()

            progress = (i + len(batch)) / total * 100
            print(f"  Progresso: {progress:.1f}% ({i + len(batch):,}/{total:,}) - Inseridos: {inserted:,}")

        elapsed = time.time() - start_time
        skipped = total - inserted
        print(f"✓ Inseridos: {inserted:,} | Ignorados (duplicatas): {skipped:,} | Tempo: {elapsed:.2f}s")

    def load_accidents(self, csv_path, batch_size=5000):
        """Carrega acidentes históricos (ignora duplicatas completas)"""
        print(f"\n{'='*60}")
        print(f"Carregando acidentes de {csv_path}...")
        print(f"{'='*60}")

        if not os.path.exists(csv_path):
            print(f"✗ Arquivo não encontrado: {csv_path}")
            return

        df = pd.read_csv(csv_path)
        df = df.dropna(subset=['h3_cell', 'year', 'month'])
        df['num_sinistros'] = df['num_sinistros'].fillna(0.0)

        total = len(df)
        print(f"Total de registros no CSV: {total:,}")

        data = [
            (row['h3_cell'], int(row['year']), int(row['month']), float(row['num_sinistros']))
            for _, row in df.iterrows()
        ]

        start_time = time.time()
        inserted = 0
        updated = 0

        for i in range(0, total, batch_size):
            batch = data[i:i + batch_size]

            # Insere apenas se não existir o mesmo h3_cell+year+month+num_sinistros
            # Atualiza se existir com num_sinistros diferente
            execute_values(
                self.cursor,
                """
                INSERT INTO heatmap (h3_cell, year, month, num_sinistros)
                VALUES %s
                ON CONFLICT (h3_cell, year, month) 
                DO UPDATE SET num_sinistros = EXCLUDED.num_sinistros
                WHERE heatmap.num_sinistros IS DISTINCT FROM EXCLUDED.num_sinistros
                """,
                batch,
                page_size=batch_size
            )

            rows_affected = self.cursor.rowcount
            inserted += rows_affected
            self.conn.commit()

            progress = (i + len(batch)) / total * 100
            print(f"  Progresso: {progress:.1f}% ({i + len(batch):,}/{total:,}) - Afetados: {inserted:,}")

        elapsed = time.time() - start_time
        skipped = total - inserted
        print(f"✓ Inseridos/Atualizados: {inserted:,} | Ignorados: {skipped:,} | Tempo: {elapsed:.2f}s")

    def load_predictions(self, csv_path, batch_size=5000):
        """Carrega predições (ignora duplicatas completas)"""
        print(f"\n{'='*60}")
        print(f"Carregando predições de {csv_path}...")
        print(f"{'='*60}")

        if not os.path.exists(csv_path):
            print(f"✗ Arquivo não encontrado: {csv_path}")
            return

        df = pd.read_csv(csv_path)
        df = df.dropna(subset=['h3_cell', 'week_start', 'predicted_accidents'])
        df['week_start'] = pd.to_datetime(df['week_start'])

        total = len(df)
        print(f"Total de registros no CSV: {total:,}")

        data = [
            (row['h3_cell'], row['week_start'].date(), float(row['predicted_accidents']))
            for _, row in df.iterrows()
        ]

        start_time = time.time()
        inserted = 0

        for i in range(0, total, batch_size):
            batch = data[i:i + batch_size]

            # Atualiza apenas se o valor de predicted_accidents for diferente
            execute_values(
                self.cursor,
                """
                INSERT INTO predictions (h3_cell, week_start, predicted_accidents)
                VALUES %s
                ON CONFLICT (h3_cell, week_start) 
                DO UPDATE SET predicted_accidents = EXCLUDED.predicted_accidents
                WHERE predictions.predicted_accidents IS DISTINCT FROM EXCLUDED.predicted_accidents
                """,
                batch,
                page_size=batch_size
            )

            rows_affected = self.cursor.rowcount
            inserted += rows_affected
            self.conn.commit()

            progress = (i + len(batch)) / total * 100
            print(f"  Progresso: {progress:.1f}% ({i + len(batch):,}/{total:,}) - Afetados: {inserted:,}")

        elapsed = time.time() - start_time
        skipped = total - inserted
        print(f"✓ Inseridos/Atualizados: {inserted:,} | Ignorados: {skipped:,} | Tempo: {elapsed:.2f}s")

    def get_stats(self):
        """Mostra estatísticas das tabelas"""
        print(f"\n{'='*60}")
        print("ESTATÍSTICAS DO BANCO DE DADOS")
        print(f"{'='*60}")

        queries = {
            "Coordenadas totais": "SELECT COUNT(*) FROM h3_coordinates",
            "Células H3 únicas": "SELECT COUNT(DISTINCT h3_cell) FROM h3_coordinates",
            "Acidentes registrados": "SELECT COUNT(*) FROM heatmap",
            "Predições": "SELECT COUNT(*) FROM predictions",
            "Bairros únicos": "SELECT COUNT(DISTINCT bairro_clean) FROM h3_coordinates WHERE bairro_clean IS NOT NULL"
        }

        for label, query in queries.items():
            self.cursor.execute(query)
            result = self.cursor.fetchone()[0]
            print(f"{label:.<45} {result:>12,}")

        print(f"{'='*60}\n")

    def close(self):
        self.cursor.close()
        self.conn.close()


if __name__ == "__main__":
    # Configuração do banco (lê variáveis de ambiente ou usa padrão)
    db_config = {
        'host': os.getenv('DB_HOST', 'viaseguradb'),
        'database': os.getenv('DB_NAME', 'viasegura'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'postgres'),
        'port': int(os.getenv('DB_PORT', 5432))
    }

    # Diretório dos CSVs
    data_dir = os.getenv('DATA_DIR', './data')

    print("\n" + "="*60)
    print("CARREGADOR DE DADOS H3 - ACIDENTES DE TRÂNSITO")
    print("="*60)
    print(f"Banco: {db_config['host']}:{db_config['port']}/{db_config['database']}")
    print(f"Diretório de dados: {data_dir}")
    print("="*60)

    loader = H3DataLoader(db_config)

    try:
        # Carrega os arquivos na ordem correta
        loader.load_coordinates(f'{data_dir}/h3_grid.csv')
        loader.load_accidents(f'{data_dir}/heatmap_monthly.csv')
        loader.load_predictions(f'{data_dir}/predictions_weekly.csv')

        # Mostra estatísticas finais
        loader.get_stats()

        print("✓ Carga de dados concluída com sucesso!")

    except KeyboardInterrupt:
        print("\n\n✗ Operação cancelada pelo usuário")
        sys.exit(1)
    except Exception as e:
        print(f"\n✗ Erro durante a carga: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
    finally:
        loader.close()