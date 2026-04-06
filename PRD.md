
---

# PRD: Sistema de Inventário e Descoberta de Rádios (Ubiquiti)
**Status:** Aprovado (v1.1 - Protocolo Ajustado)  
**Data:** 06/04/2026  
**Responsável:** Miller  

---

## 1. Visão Geral (O "Porquê")
> *Objetivo: Alinhar o time sobre o valor do que está sendo construído.*

* **Problema:** Defasagem entre os dados cadastrados no sistema e a realidade técnica dos rádios AirGrid M5 HP em campo, gerando erros de suporte e lentidão no diagnóstico.
* **Solução Proposta:** Um scanner de rede automatizado que utiliza ICMP (Ping) para descoberta e **SNMP v1** para coleta de dados técnicos, atualizando o banco de dados sem intervenção humana.
* **Visão de Sucesso:** Base de dados 100% fidedigna, atualizada automaticamente por um robô, eliminando o erro humano no inventário.

---

## 2. Personas e Contexto (O "Quem")
* **Ator Principal:** Operador de Suporte / Técnico de Rede.
* **Cenário de Uso:** O administrador cadastra um range de IPs manualmente (ex: 10.10.29.0). O sistema varre a rede e popula a lista de equipamentos automaticamente.
* **Dores Específicas:** Erros de digitação manual e dificuldade em acompanhar mudanças de sinal ou nome de dispositivos em uma rede grande.

---

## 3. Requisitos Funcionais (O "O Quê")
> *Use o raciocínio passo a passo: O que o sistema DEVE fazer.*

| ID | Funcionalidade | Descrição | Prioridade |
|:---|:---|:---|:---|
| **RF01** | **Gestão de Ranges** | Permitir cadastrar e listar prefixos de rede (ex: 10.10.29.0). A rede permanece listada mesmo sem equipamentos detectados. | P0 |
| **RF02** | **Varredura Híbrida** | O sistema deve disparar PING; se houver resposta, disparar consulta **SNMP v1** (community `public`). | P0 |
| **RF03** | **Sincronização Auto.** | Atualizar no banco: IP, Device Name, Device Model, SSID, Signal Strength, Wireless Mode e WLAN0 MAC. | P0 |
| **RF04** | **ID Única (MAC)** | Usar o **WLAN0 MAC** como chave primária lógica para evitar duplicidade caso o IP do rádio mude. | P0 |
| **RF05** | **Dashboard Status** | Exibir indicador Visual: **Verde** (SNMP OK) e **Vermelho** (Ping OK, mas falha de resposta SNMP). | P1 |
| **RF06** | **Robô de Atualização** | Varredura automática periódica (madrugada) apenas nas redes que estão cadastradas no sistema. | P1 |
| **RF07** | **CRUD de Dados** | Permitir editar dados no banco de dados do sistema ou excluir o equipamento após salvo. | P1 |

---

## 4. Requisitos Não Funcionais (A Visão de Arquiteto)
> *Diferencial de robustez: Como o sistema deve se comportar.*

* **Protocolo:** Uso obrigatório de **SNMP v1** (confirmado via teste de compatibilidade com AirGrid M5).
* **Performance:** O escaneamento deve ser assíncrono para suportar ranges classe C sem travar a interface do usuário.
* **Segurança:** Uso da community padrão `public` em modo leitura.
* **Robustez:** Implementação de timeouts curtos para evitar que o processo fique preso em rádios com alta perda de pacotes.

---

## 5. Regras de Negócio e Estados de Erro
* **Regra de Ouro (Atualização):** O sistema deve sobrescrever os dados no banco se detectar divergência entre o rádio e o sistema (ex: mudança de SSID ou Name).
* **Caminho de Erro (Fallback):** Equipamentos offline (sem PING) não devem ser excluídos do banco, apenas marcados como Inativos para preservação de histórico.
* **Tratamento de Erro:** Caso o IP responda ao PING mas falhe no SNMP (timeout ou erro de autenticação), o status deve ser alterado para **Vermelho**.

---

## 6. Métricas de Sucesso (KPIs)
* **Métrica Primária:** 100% de precisão nos dados técnicos (Nome, MAC, Sinal) sem necessidade de conferência manual.
* **Sinal de Alerta:** Taxa de erro SNMP (Status Vermelho) acima de 15% em um range de rede.

---

## 7. Fora de Escopo
* Alteração de configurações (SNMP Set/Escrita) nos rádios via sistema.
* Suporte a equipamentos que não utilizem as MIBs padrão Ubiquiti (AirOS).

---

## 8. Apêndice: Notas Técnicas
* **OID de Modelo Validada:** `1.2.840.10036.3.1.2.1.3.5` (Retorno observado: AirGrid M5).
* **Chave de Sincronismo:** O campo `WLAN0 MAC` (identificado no AirOS) é o identificador mestre para qualquer atualização no banco de dados.
* **Dependências:** Integração com biblioteca de SNMP compatível com v1 e ICMP (Ping).

---