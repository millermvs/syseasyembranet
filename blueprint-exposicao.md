# Lógica de Exposição (Services & Controllers)

## Transacionalidade
- `@Transactional(readOnly = true)` para listagens e buscas (GET).
- `@Transactional` para CRUD e varredura SNMP.

---

## Controllers REST

### `RedeController` — `/api/v1/redes`
| Método | Rota               | Service chamado                     | Retorno                        |
|--------|--------------------|-------------------------------------|--------------------------------|
| GET    | `/listar`          | `listarRedesCadastradas(page, size)` | `Page<RedeResponseDto>`       |
| POST   | `/cadastrar`       | `cadastrarRede(request)`            | `RedeResponseDto`              |
| POST   | `/mapear/{id}`     | `mapearRedeEnviadaComId(id)`        | `List<EquipamentoResponseDto>` |
| DELETE | `/excluir?id=`     | `deletarRede(id)`                   | `RedeResponseDto`              |

### `EquipamentosController` — `/api/v1/equipamentos`
| Método | Rota               | Service chamado                          | Retorno                        |
|--------|--------------------|------------------------------------------|--------------------------------|
| GET    | `/listar`          | `listarEquipamentos(page, size)`         | `Page<EquipamentoResponseDto>` |
| GET    | `/mapear/{ip}`     | `buscarInformacoesEquipamentoUnico(ip)`  | `EquipamentoResponseDto`       |
| POST   | `/cadastrar`       | `cadastrarEquipamento(request)`          | `EquipamentoResponseDto`       |
| PUT    | `/editar?id=`      | `editarEquipamento(id, request)`         | `EquipamentoResponseDto`       |

---

## RedeService

### `cadastrarRede(RedeRequestDto)`
Verifica duplicidade pelo campo `rede`. Lança `JaCadastradoException` se já existir.

### `deletarRede(Long id)`
Verifica se a rede possui equipamentos vinculados (`!getEquipamentos().isEmpty()`).
Lança `RegraDeNegocioException` caso existam vínculos. Só exclui se estiver vazia.

### `listarRedesCadastradas(page, size)`
Usa `RedeRepository.findAllComTotal()` com Projection para retornar rede + total de equipamentos.
Ordenado por `rede` alfabeticamente.

### `consultarRede(String ip)`
Converte o IP para o endereço de rede (`.0`) e busca pelo campo `rede`.
Lança `NaoEncontradoException` se não existir.

### `redeExiste(String ip)` → `Boolean`
Versão silenciosa de `consultarRede`: retorna `true/false` sem lançar exceção.

### `converterIpParaRede(String ip)` → `String`
Utilitário: pega "192.168.1.55" e retorna "192.168.1.0".

---

## EquipamentoService

### `cadastrarEquipamento(EquipamentoRequestDto)`
Auto-provisiona a Rede caso ela ainda não exista (chama `redeService.cadastrarRede` internamente).
Verifica duplicidade por IP. Lança `RegraDeNegocioException` se já cadastrado.

### `editarEquipamento(String id, EquipamentoRequestDto)`
Busca por ID. Atualiza todos os campos exceto `ip` e `rede`. Lança `RegraDeNegocioException` se não encontrado.

### `listarEquipamentos(page, size)`
Listagem paginada ordenada por `ip` ascendente. Usa `EquipamentoResponseModel.montarDtoEquipamento()`.

### `buscarInformacoesEquipamentoUnico(String ip)`
Fluxo completo para um único IP:
1. Faz ping (`verificarSeIpResponde`). Lança `RegraDeNegocioException` se não responder.
2. Auto-provisiona a Rede se necessário.
3. Chama `buscarInformacoes(ip, rede)` para executar a consulta SNMP.

### `buscarInformacoes(String ip, Rede rede)` → `EquipamentoResponseDto`
Núcleo da integração SNMP. Fluxo:
1. Abre `DefaultUdpTransportMapping` e instancia `Snmp`.
2. Configura `CommunityTarget` (community: "public", porta 161, v1, timeout 2s, 1 retry).
3. Monta PDU GET com os 7 OIDs do equipamento.
4. Envia e recebe a resposta. Lança `RegraDeNegocioException` se PDU nulo.
5. Monta o DTO via `EquipamentoResponseModel.montarRespostaEquipamento()`.
6. **Upsert por IP:** se o equipamento não existe, cria; se existe e os dados mudaram, atualiza.
7. Fecha `Snmp` e `TransportMapping` no bloco `finally`.

### `verificarSeIpResponde(String ip)` → `Boolean`
Usa `InetAddress.isReachable(1000ms)`. Lança `RegraDeNegocioException` em caso de exceção de rede.

---

## MapearRedeService

### `mapearRedeEnviadaComId(Long id)` → `List<EquipamentoResponseDto>`
Varre sequencialmente os IPs de `.2` a `.254` da rede informada.
Para cada IP alcançável (ping < 1s), chama `equipamentoService.buscarInformacoes(ip, rede)`.
Em caso de falha SNMP, adiciona um `EquipamentoResponseDto` com o IP e a mensagem de erro no campo `status`.
**Atenção:** varredura síncrona — pode ser lenta em redes grandes (253 IPs).