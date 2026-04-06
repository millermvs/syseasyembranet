# Transformação de Dados (DTOs & Model)

## Padrão de DTO
DTOs são **classes** com `@Getter` e `@Setter` do Lombok (não Records).
Organizados em subpacotes: `dtos/request/{dominio}` e `dtos/response/{dominio}`.

---

## DTOs de Rede

### `RedeRequestDto` — `dtos/request/rede`
| Campo           | Tipo   | Validação                  | Notas                           |
|-----------------|--------|----------------------------|---------------------------------|
| `rede`          | String | `@NotBlank`                | Ex: "192.168.1.0"              |
| `modoWireless`  | String | `@NotBlank`                | Modo: "AP" ou "STATION" (novo) |

### `RedeResponseDto` — `dtos/response/rede`
| Campo               | Tipo   | Notas                                                |
|---------------------|--------|------------------------------------------------------|
| `idRede`            | Long   | ID da rede                                          |
| `rede`              | String | Endereço de rede                                    |
| `modoWireless`      | String | Modo definido no cadastro (novo) — READ-ONLY       |
| `totalEquipamentos` | Long   | Contagem via Projection (listagem)                 |

### `IpSendoMapeado` — `dtos/response/rede`
| Campo | Tipo   | Notas                            |
|-------|--------|----------------------------------|
| `ip`  | String | IP individual em processo de mapeamento |

---

## DTOs de Equipamento

### `EquipamentoRequestDto` — `dtos/request/equipamento`
| Campo          | Tipo   | Notas                             |
|----------------|--------|-----------------------------------|
| `ip`           | String | IP do equipamento                 |
| `mac`          | String | Endereço MAC                      |
| `nomeRadio`    | String | Nome do dispositivo               |
| `ssid`         | String | SSID da rede Wi-Fi                |
| `nivelDeSinal` | String | Nível de sinal                    |
| `canalRadio`   | String | Canal de rádio                    |
| `macDoAp`      | String | MAC do Access Point               |
| `modeloDoRadio`| String | Modelo do equipamento             |

### `EquipamentoResponseDto` — `dtos/response/equipamento`
| Campo          | Tipo   | Notas                                              |
|----------------|--------|---------------------------------------------------|
| `id`           | Long   | ID do equipamento                                  |
| `ip`           | String | IP do equipamento                                  |
| `rede`         | String | Endereço da rede (texto, não o ID)                 |
| `modoWireless` | String | Modo herdado da Rede (novo) — **sempre lê de Rede** |
| `mac`          | String | Endereço MAC                                       |
| `nomeRadio`    | String | Nome do dispositivo                                |
| `ssid`         | String | SSID da rede Wi-Fi                                 |
| `nivelDeSinal` | String | Nível de sinal                                     |
| `canalRadio`   | String | Canal de rádio                                     |
| `macDoAp`      | String | MAC do Access Point                                |
| `Status`       | String | Status da consulta (ex: "SUCESSO", erro)           |
| `modeloDoRadio`| String | Modelo do equipamento                              |

---

## Model de Montagem: `EquipamentoResponseModel` (@Component)
Localizado em `models/`. É o equivalente ao Mapper para o domínio de Equipamento.
Possui dois métodos distintos de montagem:

### `montarRespostaEquipamento(String ip, PDU resposta, String modoWireless)`
Constrói o `EquipamentoResponseDto` a partir de uma resposta PDU do SNMP.
Extrai os `VariableBinding` na ordem em que foram adicionados ao PDU (índices 0 a 6).
Recebe `modoWireless` como parâmetro (vindo da Rede associada).
Define `status = "SUCESSO"`.

**Nota:** O `modoWireless` é passado pelo `EquipamentoService.buscarInformacoes()` via `rede.getModoWireless()`.

### `montarDtoEquipamento(Equipamento equipamento)`
Constrói o `EquipamentoResponseDto` a partir de uma entidade já carregada do banco.
Extrai `equipamento.getRede().getRede()` para popular `rede` (texto).
Extrai `equipamento.getRede().getModoWireless()` para popular `modoWireless` (novo).
Define `status = "SUCESSO"`.