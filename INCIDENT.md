# 🚨 Análise de Incidente — Erro Recorrente em Produção

---

## 📌 Identificação do Incidente

| Campo | Valor |
|---|---|
| Sistema | Módulo de Gestão de Processos Judiciais |
| Ambiente | Produção |
| Severidade | 🔴 Alta |
| Impacto | Operação de procuradores afetada |
| Data/Hora da identificação | 26/05/2026 às 03:14 |
| Duração | ~3 minutos |
| Responsável pela análise | Cauã Henrique Viana Salgado |

---

# 1. 📖 Descrição do Problema

Durante o pico de utilização matinal, o endpoint:

```http
POST /api/processos
```

passou a retornar múltiplos erros `HTTP 500` em rajadas de alta frequência.

Os erros ocorreram simultaneamente em diferentes threads da aplicação, sempre associados ao mesmo número de processo:

```text
0047/2026
```

O comportamento indicava forte indício de concorrência simultânea na criação do mesmo recurso.

---

# 2. 📄 Logs do Incidente

```log
2026-05-26T03:14:22.451-03:00 ERROR [nio-8080-exec-7]
c.a.p.exception.GlobalExceptionHandler :
Erro inesperado: could not execute statement; SQL [n/a]

org.springframework.dao.DataIntegrityViolationException:
could not execute statement; SQL [n/a]

Caused by:
org.postgresql.util.PSQLException:
ERROR: duplicate key value violates unique constraint
"ukb8u5y8cj0ohokx2ixyncng1et"

Detail:
Key (numero)=(0047/2026) already exists.

at ProcessoService.criar(ProcessoService.java:47)
at ProcessoController.criar(ProcessoController.java:29)
```

### 🔍 Padrão observado

- Ocorrência em múltiplas threads:
    - `exec-7`
    - `exec-9`
    - `exec-11`

- Intervalo entre falhas:
    - entre **10ms e 30ms**

- Mesmo identificador de processo:
    - `0047/2026`

---

# 3. 🧠 Análise da Causa Raiz

## 3.1 O que aconteceu

O sistema recebeu múltiplas requisições simultâneas para criação do mesmo processo em um intervalo aproximado de **27ms**.

A origem foi um bug de *double-click* no botão de salvar do front-end, que disparava múltiplos submits concorrentes.

Todas as threads executaram simultaneamente:

```java
existsByNumero()
```

Como nenhuma inserção havia sido concluída naquele instante, todas receberam:

```text
false
```

Em seguida, todas tentaram executar o `INSERT`.

Apenas a primeira transação obteve sucesso.

As demais colidiram com a constraint `UNIQUE` do PostgreSQL.

---

## 3.2 Causa técnica

A lógica:

```java
existsByNumero()
→ save()
```

é uma operação **não-atômica**.

Existe uma janela de concorrência entre:

1. verificar existência;
2. persistir o registro.

Esse padrão é conhecido como:

- **Race Condition**
- **Check-Then-Act Problem**

Problema clássico em aplicações:

- stateless;
- distribuídas;
- horizontalmente escaláveis.

---

## 3.3 Diagrama da Race Condition

```text
Tempo:      T1                T2                T3

Thread 1:   CHECK(false) ------------------> INSERT → OK

Thread 2:   CHECK(false) -------------> INSERT → ERRO

Thread 3:   CHECK(false) ---------> INSERT → ERRO
```

Todas as threads executaram a validação antes da primeira persistência finalizar.

---

## 3.4 Por que o cliente recebeu HTTP 500

A exceção:

```java
DataIntegrityViolationException
```

não possuía tratamento específico no:

```java
GlobalExceptionHandler
```

Como consequência:

- a exceção propagou até o handler genérico;
- o sistema respondeu com:

```http
500 Internal Server Error
```

Contudo, semanticamente, o cenário representa:

```http
409 Conflict
```

pois trata-se de conflito de negócio e não falha interna da aplicação.

---

# 4. 📊 Impacto do Incidente

| Métrica | Resultado |
|---|---|
| Duração | ~3 minutos |
| Requisições com erro | 47 |
| Dados corrompidos | 0 |
| Processos perdidos | 0 |
| Usuários impactados | 3 procuradores |
| Integridade dos dados | Preservada pela constraint UNIQUE |

---

# 5. 🛠 Correções Aplicadas

---

## 5.1 Correção Imediata — Tratamento de `DataIntegrityViolationException`

### Objetivo

Evitar exposição de erro `500` ao cliente em cenários de violação de constraint.

### Implementação

```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, Object>> handleDataIntegrity(
        DataIntegrityViolationException ex) {

    log.warn("Violação de integridade de dados: {}", ex.getMessage());

    return buildResponse(
        HttpStatus.CONFLICT,
        "Já existe um processo cadastrado com esse número."
    );
}
```

### Resultado esperado

| Antes | Depois |
|---|---|
| HTTP 500 | HTTP 409 |
| Mensagem genérica | Mensagem clara de negócio |

---

## 5.2 Correção Imediata — Idempotência no Front-End

### Objetivo

Evitar múltiplos submits simultâneos.

### Implementação

```tsx
const [salvando, setSalvando] = useState(false);

const handleSubmit = async (e) => {
    e.preventDefault();

    setSalvando(true);

    try {
        await processoService.criar(form);
        navigate('/');
    } catch (err) {
        setErro(
            err.response?.data?.erro ||
            'Erro ao criar processo.'
        );
    } finally {
        setSalvando(false);
    }
};

<Button type="submit" disabled={salvando}>
    {salvando ? 'Salvando...' : 'Salvar Processo'}
</Button>
```

### Resultado esperado

- Eliminação de *double-submit*
- Redução drástica de concorrência acidental

---

## 5.3 Correção Planejada — Lock Pessimista

### Objetivo

Garantir atomicidade da operação em ambiente distribuído.

### Repository

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
    SELECT p
    FROM Processo p
    WHERE p.numero = :numero
""")
Optional<Processo> findByNumeroWithLock(
        @Param("numero") String numero);
```

### Service

```java
@Transactional
public ProcessoResponseDTO criar(ProcessoRequestDTO dto) {

    processoRepository
        .findByNumeroWithLock(dto.getNumero())
        .ifPresent(p -> {
            throw new ProcessoJaCadastradoException(
                dto.getNumero()
            );
        });

    // continua criação...
}
```

### Benefícios

- elimina race condition;
- serializa concorrência;
- mantém integridade mesmo com múltiplas instâncias.

---

# 6. 🛡 Medidas Preventivas

## Curto Prazo

### ✅ Constraint UNIQUE no banco

Última linha de defesa da integridade.

---

### ✅ Tratamento explícito de exceptions

Mapear conflitos para:

```http
409 Conflict
```

---

### ✅ Idempotência no front-end

Bloqueio de múltiplos submits.

---

## Médio Prazo

### 📈 Alertas no Kibana

Criar alerta para excesso de conflitos:

```kibana
level:WARN AND message:"Violação de integridade"
AND @timestamp:[now-1m TO now]
```

### Critério sugerido

- mais de 5 ocorrências/minuto.

---

### 🔒 Lock pessimista

Implementar controle transacional via:

```sql
SELECT FOR UPDATE
```

---

## Longo Prazo

### 🧪 Testes de concorrência no CI

```java
@Test
void deveTratarCriacaoConcorrente()
        throws InterruptedException {

    int threads = 10;

    CountDownLatch latch =
            new CountDownLatch(threads);

    AtomicInteger sucessos =
            new AtomicInteger(0);

    AtomicInteger conflitos =
            new AtomicInteger(0);

    for (int i = 0; i < threads; i++) {

        new Thread(() -> {

            try {

                processoService.criar(requestDTO);

                sucessos.incrementAndGet();

            } catch (ProcessoJaCadastradoException e) {

                conflitos.incrementAndGet();

            } finally {

                latch.countDown();

            }

        }).start();
    }

    latch.await();

    assertThat(sucessos.get()).isEqualTo(1);

    assertThat(conflitos.get())
            .isEqualTo(threads - 1);
}
```

---

### 📨 Avaliação de arquitetura orientada a eventos

Estudo futuro para operações críticas utilizando:

- Kafka
- filas
- consumo serializado

Objetivo:

- eliminar concorrência por design.

---

# 7. 📚 Lições Aprendidas

## 1. Constraints no banco são obrigatórias

Validação em aplicação não garante integridade concorrente.

---

## 2. Exceptions de integridade devem ser tratadas

Conflito de negócio ≠ erro interno.

---

## 3. Front-end também participa da confiabilidade

Idempotência reduz significativamente incidentes concorrentes.

---

## 4. Race conditions não aparecem em testes unitários

Necessidade de:

- testes de integração;
- testes concorrentes;
- testes de carga.

---

## 5. Logs estruturados reduzem MTTR

O diagnóstico ocorreu rapidamente graças a:

- identificação de thread;
- stack trace;
- contexto do banco;
- timestamp detalhado.

---

# 8. 📌 Status Atual

| Correção | Status |
|---|---|
| Tratamento de `DataIntegrityViolationException` | 🟡 Pendente de deploy |
| Idempotência no front-end | 🟡 Pendente de deploy |
| Lock pessimista (`SELECT FOR UPDATE`) | 🔵 Planejado |
| Alertas no Kibana | 🔵 Planejado |
| Testes concorrentes no CI | 🔵 Planejado |
| Avaliação de Kafka | 🟣 Em análise |

---

# ✅ Conclusão

O incidente não gerou perda de dados devido à proteção da constraint `UNIQUE` do PostgreSQL, demonstrando que a camada de persistência preservou corretamente a integridade transacional.

Entretanto, o episódio evidenciou fragilidades importantes:

- ausência de tratamento semântico de conflitos;
- vulnerabilidade a race conditions;
- falta de idempotência no front-end;
- ausência de testes concorrentes automatizados.

As correções propostas atacam tanto os sintomas quanto a causa raiz do problema, elevando significativamente a resiliência da aplicação em cenários de alta concorrência.

---

## ✍️ Responsável pelo Documento

**Cauã Henrique Viana Salgado**

Data: `26/05/2026`