# Persistência e Domínio: cadastroviasnmp

## Padrões de Entidade JPA
- `@Entity` + `@Table(name = "nome_em_portugues")`.
- **IDs:** `@GeneratedValue(strategy = GenerationType.SEQUENCE)` (sem `@SequenceGenerator` explícito).
- **Relacionamentos lado "Muitos":** `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn`.
- **Relacionamentos lado "Um":** `@OneToMany(mappedBy = ..., fetch = FetchType.LAZY)` (usado em `Rede`).
- Classes com `@Getter` e `@Setter` do Lombok (sem `@Data`).

## Dicionário de Dados

### Tabela: `redes`
Entidade: `Rede`
| Campo            | Tipo   | Notas                                            |
|------------------|--------|--------------------------------------------------|
| `id_rede`        | Long   | PK, gerado por sequence                          |
| `rede`           | String | Endereço de rede (ex: "192.168.1.0")             |
| `modo_wireless`  | String | Modo (ex: "AP" ou "STATION") - **IMUTÁVEL**     |

- Possui `@OneToMany(mappedBy = "rede")` apontando para `Equipamento` (usado para contagem e validação de exclusão).
- Campo `modo_wireless`: definido **uma única vez** no cadastro da rede; após criar, não pode ser alterado (`@Column(updatable = false)`).

### Tabela: `equipamentos`
Entidade: `Equipamento`
| Campo            | Tipo   | Notas                                        |
|------------------|--------|----------------------------------------------|
| `id_equipamento` | Long   | PK, gerado por sequence                      |
| `ip`             | String | IP do equipamento (chave lógica de busca)    |
| `id_rede`        | Long   | FK para `redes` (`@ManyToOne LAZY`)          |
| `mac`            | String | Endereço MAC                                 |
| `nome_radio`     | String | Nome do dispositivo                          |
| `ssid`           | String | SSID da rede Wi-Fi                           |
| `nivel_de_sinal` | String | Nível de sinal em dBm                        |
| `canal_radio`    | String | Canal de rádio                               |
| `mac_do_ap`      | String | MAC do Access Point associado                |
| `status`         | String | Status da última consulta SNMP               |
| `modelo_do_radio`| String | Modelo do equipamento                        |

## OIDs SNMP Consultados
| Campo          | OID                              |
|----------------|----------------------------------|
| mac            | 1.3.6.1.2.1.2.2.1.6.2           |
| nomeRadio      | 1.3.6.1.2.1.1.5.0               |
| ssid           | 1.3.6.1.4.1.41112.1.4.5.1.2.1   |
| nivelDeSinal   | 1.3.6.1.4.1.41112.1.4.5.1.5.1   |
| canalRadio     | 1.3.6.1.4.1.41112.1.4.1.1.4.1   |
| macDoAp        | 1.3.6.1.4.1.41112.1.4.5.1.4.1   |
| modeloDoRadio  | 1.2.840.10036.3.1.2.1.3.5       |

(SNMP v1, community: "public", porta UDP 161, timeout: 2s, retries: 1)

## Repositories

### `RedeRepository`
- `Optional<Rede> findByRede(String rede)` — busca exata pelo endereço de rede.
- `Page<RedeResumoProjection> findAllComTotal(Pageable)` — retorna rede + contagem de equipamentos via `@Query` JPQL com `LEFT JOIN` e `GROUP BY`.

### `EquipamentoRepository`
- `Optional<Equipamento> findByIp(String ip)` — busca por IP (chave lógica).
- `Page<Equipamento> findAll(Pageable)` — listagem paginada.

## Projections

### `RedeResumoProjection` (interface)
- `Long getIdRede()`
- `String getRede()`
- `String getModoWireless()` — **novo**
- `Long getTotalEquipamentos()`

Usada na listagem de redes para evitar carregar a coleção `@OneToMany` desnecessariamente. Traz o modo imutável junto com o resumo da rede.