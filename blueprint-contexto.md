# Projeto: cadastroviasnmp
## Propósito
Cadastro e monitoramento de equipamentos de rede (rádios) via varredura SNMP.
O sistema descobre os equipamentos na rede, consulta seus dados via protocolo SNMP (snmp4j)
e os persiste no banco para consulta e gestão.

## Stack Técnica
- Java 21 / Spring Boot 4.0.2
- PostgreSQL (DDL auto: update)
- Maven / Lombok
- snmp4j 3.7.7 (comunicação SNMP)
- Pacote base: br.com.easyembranet

## Arquitetura de Camadas
1. **Controller:** Exposição REST. Recebe requisição, delega ao Service, retorna ResponseEntity.
2. **Service:** Onde reside 100% da regra de negócio e transacionalidade.
3. **Repository:** Interface Spring Data JPA (+ @Query JPQL quando necessário).
4. **Model (@Component):** `EquipamentoResponseModel` — monta o DTO a partir de PDU SNMP ou de Entidade.
5. **DTO (classes @Getter/@Setter):** Request (entrada) e Response (saída). Separados por domínio em subpacotes.
6. **Projection (interface):** `RedeResumoProjection` — retorna dados agregados do banco sem carregar a entidade completa.
7. **Exceptions / Handlers:** Exceções de negócio tipadas + `GlobalExceptionHandler` com `@ControllerAdvice`.

## Regras de Ouro
- Nomenclatura de variáveis e campos em **Português Brasileiro**.
- Uso de `var` para variáveis locais.
- Injeção via `@Autowired` em campos.
- DTOs são classes com `@Getter` e `@Setter` do Lombok (não Records).
- IDs gerados via `@GeneratedValue(strategy = GenerationType.SEQUENCE)`.

## Exceções de Negócio
| Classe                   | HTTP | Quando usar                                      |
|--------------------------|------|--------------------------------------------------|
| `JaCadastradoException`  | 409  | Recurso já existe no banco                       |
| `NaoEncontradoException` | 404  | Recurso não encontrado                           |
| `RegraDeNegocioException`| 422  | Violação de regra (ex: SNMP inacessível, vínculo)|