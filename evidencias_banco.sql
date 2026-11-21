-- AmparaPet: evidências do banco de dados em execução
-- Uso: veja as instruções na resposta (docker cp + docker exec ... -f)

\pset pager off
\pset border 2

\echo '=== 1. Conexão, versão e data/hora da execução ==='
\conninfo
SELECT version();
SELECT now() AS data_hora_execucao, current_database() AS banco, current_user AS usuario;

\echo '=== 2. Tamanho do banco ==='
SELECT pg_size_pretty(pg_database_size(current_database())) AS tamanho_do_banco;

\echo '=== 3. Tabelas criadas ==='
\dt

\echo '=== 4. Estrutura das tabelas ==='
\d instituicao
\d usuario
\d animal
\d resgate
\d atendimento
\d adocao

\echo '=== 5. Chaves estrangeiras (relacionamentos) ==='
SELECT tc.table_name        AS tabela,
       kcu.column_name      AS coluna,
       ccu.table_name       AS referencia_tabela,
       ccu.column_name      AS referencia_coluna
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage ccu
  ON ccu.constraint_name = tc.constraint_name AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
ORDER BY tc.table_name, kcu.column_name;

\echo '=== 6. Migração Flyway aplicada ==='
SELECT installed_rank, version, description, success, installed_on
FROM flyway_schema_history
ORDER BY installed_rank;

\echo '=== 7. Quantidade de registros por tabela ==='
SELECT 'instituicao' AS tabela, count(*) AS registros FROM instituicao
UNION ALL SELECT 'usuario',      count(*) FROM usuario
UNION ALL SELECT 'animal',       count(*) FROM animal
UNION ALL SELECT 'resgate',      count(*) FROM resgate
UNION ALL SELECT 'atendimento',  count(*) FROM atendimento
UNION ALL SELECT 'adocao',       count(*) FROM adocao;

\echo '=== 8. Animais por status (ciclo de vida) ==='
SELECT status, count(*) AS animais
FROM animal
GROUP BY status
ORDER BY animais DESC;

\echo '=== 9. Usuários por perfil (sem exibir senha) ==='
SELECT perfil, count(*) AS usuarios, count(*) FILTER (WHERE ativo) AS ativos
FROM usuario
GROUP BY perfil
ORDER BY perfil;

\echo '=== 10. Atendimentos por tipo e validação ==='
SELECT tipo, validado, count(*) AS atendimentos
FROM atendimento
GROUP BY tipo, validado
ORDER BY tipo, validado;

\echo '=== 11. Últimos animais cadastrados ==='
SELECT id, nome, especie, status, criado_em
FROM animal
ORDER BY criado_em DESC
LIMIT 10;

\echo '=== 12. Adoções registradas ==='
SELECT a.id AS animal_id, a.nome, a.status, ad.adotante_nome, ad.data_adocao
FROM adocao ad
JOIN animal a ON a.id = ad.animal_id
ORDER BY ad.data_adocao DESC;
